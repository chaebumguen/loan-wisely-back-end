package com.ccksy.loan.domain.recommend.process.impl;

import com.ccksy.loan.domain.recommend.command.RecommendCommand;
import com.ccksy.loan.domain.recommend.filter.chain.ChainFactory;
import com.ccksy.loan.domain.recommend.filter.chain.IneligibilityFilter;
import com.ccksy.loan.domain.recommend.filter.model.ExclusionReason;
import com.ccksy.loan.domain.recommend.filter.model.FilterContext;
import com.ccksy.loan.domain.recommend.policy.eligibility.DefaultEligibilityPolicy;
import com.ccksy.loan.domain.recommend.policy.eligibility.EligibilityPolicy;
import com.ccksy.loan.domain.recommend.policy.scoring.CompositeScoreStrategy;
import com.ccksy.loan.domain.recommend.policy.scoring.PurposeScoreStrategy;
import com.ccksy.loan.domain.recommend.policy.scoring.RiskScoreStrategy;
import com.ccksy.loan.domain.recommend.policy.scoring.ScoreStrategy;
import com.ccksy.loan.domain.recommend.policy.sorting.ScoreDescSortStrategy;
import com.ccksy.loan.domain.recommend.policy.sorting.SortStrategy;
import com.ccksy.loan.domain.recommend.process.template.AbstractRecommendProcess;
import com.ccksy.loan.domain.recommend.result.builder.RecommendItemBuilder;
import com.ccksy.loan.domain.recommend.result.builder.RecommendResultBuilder;
import com.ccksy.loan.domain.recommend.result.core.RecommendItem;
import com.ccksy.loan.domain.recommend.result.core.RecommendResult;
import com.ccksy.loan.domain.recommend.service.RiskScoringService;
import com.ccksy.loan.domain.recommend.service.model.RiskScoreResult;
import com.ccksy.loan.domain.recommend.state.NotReadyState;
import com.ccksy.loan.domain.recommend.state.ReadyState;
import com.ccksy.loan.domain.product.entity.LoanProduct;
import com.ccksy.loan.domain.product.mapper.LoanProductMapper;
import com.ccksy.loan.domain.product.service.ProductRateQuote;
import com.ccksy.loan.domain.product.service.ProductRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * v1: 룰기반 추천 프로세스(간단 버전)
 * - 실제 환경의 UserProfile/UserConsent/Product 정보를 반영하는 초기 구조
 */
@Component
@Profile("!ml")
@RequiredArgsConstructor
@Slf4j
public class RuleBasedRecommendProcess extends AbstractRecommendProcess {

    private final EligibilityPolicy eligibilityPolicy = new DefaultEligibilityPolicy();
    private final RiskScoringService riskScoringService;
    private final LoanProductMapper loanProductMapper;
    private final ProductRateService productRateService;

    private static final int MAX_RECOMMEND_ITEMS = 10;

    @Override
    protected RecommendResult doExecute(RecommendCommand command) {

        // v1 초기 FilterContext(실제 환경 요소를 단순 feature로 구성)
        RiskScoreResult risk = riskScoringService.score(command.getUserId(), command.getRequestedInputLevel());

        FilterContext filterContext = new FilterContext(
                command.getUserId(),
                command.getRequestedInputLevel(),
                risk.getCreditScore(),
                risk.getDsr(),
                risk.getLoanPurposeCode(),
                LocalDateTime.now()
        );
        log.info("RECO purpose debug userId={} inputLv={} loanPurposeCode={}", command.getUserId(),
                command.getRequestedInputLevel(), risk.getLoanPurposeCode());

        boolean notReady = false;
        List<ExclusionReason> warnings = new ArrayList<>();
        if (!eligibilityPolicy.isEligible(filterContext)) {
            notReady = true;
            warnings.add(ExclusionReason.of("ELIGIBILITY_FAILED", "추천 기본 조건을 충족하지 않습니다."));
        }

        // 필터 체인 적용: creditScore/dsr 등 제외 사유 수집
        IneligibilityFilter chain = ChainFactory.createDefaultChain();
        List<ExclusionReason> chainReasons = chain.collect(filterContext);
        if (!chainReasons.isEmpty()) {
            notReady = true;
            warnings.addAll(chainReasons);
        }

        // TODO: 정책 + 조합 로직 + 정렬 + 제외 사유(productId 기준)
        List<LoanProduct> candidates = loadCandidates(filterContext);
        if (log.isInfoEnabled()) {
            java.util.Set<String> distinctTypes = new java.util.HashSet<>();
            for (LoanProduct p : candidates) {
                if (p != null && p.getProductTypeCodeValueId() != null) {
                    distinctTypes.add(p.getProductTypeCodeValueId().trim().toUpperCase());
                }
            }
            log.info("RECO purpose debug candidates={} distinctTypes={}",
                    candidates.size(), distinctTypes);
        }
        if (candidates.isEmpty()) {
            var b = RecommendResultBuilder.notReady(command.getReproduceKey(), new NotReadyState().code())
                    .resolvedInputLevel(command.getRequestedInputLevel());
            b = RecommendResultBuilder.addGlobalWarning(b, "NO_PRODUCT", "추천 가능한 상품이 없습니다.");
            for (ExclusionReason warn : warnings) {
                b = RecommendResultBuilder.addGlobalWarning(b, warn.getCode(), warn.getMessage());
            }
            return b.build();
        }

        ScoreStrategy scoreStrategy = buildScoreStrategy();
        SortStrategy sortStrategy = new ScoreDescSortStrategy();

        Map<Long, BigDecimal> scores = new HashMap<>();
        Map<Long, BigDecimal> rateMins = new HashMap<>();
        List<Long> productIds = new ArrayList<>();

        for (LoanProduct product : candidates) {
            Long productId = product.getProductId();
            if (productId == null) {
                continue;
            }
            productIds.add(productId);

            BigDecimal baseScore = scoreStrategy.score(filterContext, productId);
            ProductRateQuote quote = productRateService.getRateQuote(productId);
            BigDecimal rateMin = quote != null ? quote.getRateMin() : null;
            BigDecimal rateBonus = rateBonus(rateMin);
            BigDecimal totalScore = baseScore.add(rateBonus);

            java.util.Set<String> allowedTypes = resolveAllowedProductTypes(filterContext.getLoanPurposeCode());
            String actualType = product.getProductTypeCodeValueId();
            if (allowedTypes != null && !allowedTypes.isEmpty() && actualType != null && !actualType.isBlank()) {
                String actualUpper = actualType.trim().toUpperCase();
                if (!actualUpper.isBlank()) {
                    if (allowedTypes.contains(actualUpper)) {
                        totalScore = totalScore.add(new BigDecimal("0.05"));
                    } else {
                        totalScore = totalScore.subtract(new BigDecimal("0.05"));
                    }
                }
            }

            scores.put(productId, totalScore);
            rateMins.put(productId, rateMin);
        }

        List<Long> sorted = sortStrategy.sort(productIds, scores, rateMins);
        List<RecommendItem> items = new ArrayList<>();
        Map<Long, LoanProduct> productById = new HashMap<>();
        for (LoanProduct product : candidates) {
            if (product.getProductId() != null) {
                productById.put(product.getProductId(), product);
            }
        }

        List<ExclusionReason> baseReasons = dedupeReasons(warnings);
        Map<Long, Boolean> purposeMismatch = new HashMap<>();
        for (Long productId : sorted) {
            LoanProduct product = productById.get(productId);
            ExclusionReason purposeReason = buildPurposeReason(filterContext, product);
            purposeMismatch.put(productId, purposeReason != null);
        }
        sorted = reorderByPurpose(sorted, purposeMismatch);

        for (int i = 0; i < sorted.size() && i < MAX_RECOMMEND_ITEMS; i++) {
            Long productId = sorted.get(i);
            BigDecimal score = scores.getOrDefault(productId, BigDecimal.ZERO);
            BigDecimal rateMin = rateMins.get(productId);
            LoanProduct product = productById.get(productId);

            List<ExclusionReason> reasons = new ArrayList<>(baseReasons);
            ExclusionReason purposeReason = buildPurposeReason(filterContext, product);
            if (purposeReason != null) {
                reasons.add(purposeReason);
            }
            reasons = dedupeReasons(reasons);

            String reasonText = buildReasonText(reasons);
            items.add(RecommendItemBuilder.of(productId, score, rateMin, reasonText, reasons));
        }

        var b = (notReady
                ? RecommendResultBuilder.notReady(command.getReproduceKey(), new NotReadyState().code())
                : RecommendResultBuilder.ready(command.getReproduceKey(), new ReadyState().code()))
                .resolvedInputLevel(command.getRequestedInputLevel())
                .items(items);

        for (ExclusionReason warn : dedupeReasons(warnings)) {
            b = RecommendResultBuilder.addGlobalWarning(b, warn.getCode(), warn.getMessage());
        }

        return b.build();
    }

    private List<LoanProduct> loadCandidates(FilterContext filterContext) {
        List<LoanProduct> all = loanProductMapper.selectList(null, null, null, null);
        return all == null ? new ArrayList<>() : all;
    }

    private ScoreStrategy buildScoreStrategy() {
        Map<String, BigDecimal> purposeWeight = new HashMap<>();
        purposeWeight.put("CREDIT", new BigDecimal("0.30"));
        purposeWeight.put("MORTGAGE", new BigDecimal("0.25"));
        purposeWeight.put("RENT", new BigDecimal("0.20"));
        return new CompositeScoreStrategy(List.of(
                new RiskScoreStrategy(new BigDecimal("0.70")),
                new PurposeScoreStrategy(purposeWeight)
        ));
    }

    private java.util.Set<String> resolveAllowedProductTypes(String purposeCode) {
        if (purposeCode == null || purposeCode.isBlank()) {
            return java.util.Collections.emptySet();
        }
        String v = purposeCode.trim().toUpperCase();
        if (v.contains("LIVING") || v.contains("생활") || v.contains("BUSINESS") || v.contains("사업")
                || v.contains("CREDIT") || v.contains("신용")) {
            return java.util.Set.of("CREDIT");
        }
        if (v.contains("HOUSING") || v.contains("주택") || v.contains("모기지") || v.contains("MORTGAGE")) {
            return java.util.Set.of("MORTGAGE", "RENT");
        }
        if (v.contains("RENT") || v.contains("JEONSE") || v.contains("전세") || v.contains("임대")) {
            return java.util.Set.of("RENT", "MORTGAGE");
        }
        if (v.equals("CREDIT") || v.equals("MORTGAGE") || v.equals("RENT")) {
            return java.util.Set.of(v);
        }
        return java.util.Collections.emptySet();
    }

    private BigDecimal rateBonus(BigDecimal rateMin) {
        if (rateMin == null) {
            return BigDecimal.ZERO;
        }
        if (rateMin.compareTo(new BigDecimal("5.0")) <= 0) {
            return new BigDecimal("0.20");
        }
        if (rateMin.compareTo(new BigDecimal("8.0")) <= 0) {
            return new BigDecimal("0.10");
        }
        if (rateMin.compareTo(new BigDecimal("12.0")) <= 0) {
            return new BigDecimal("0.05");
        }
        return BigDecimal.ZERO;
    }

    private ExclusionReason buildPurposeReason(FilterContext ctx, LoanProduct product) {
        if (ctx == null || product == null) {
            return null;
        }
        java.util.Set<String> desired = resolveAllowedProductTypes(ctx.getLoanPurposeCode());
        if (desired == null || desired.isEmpty()) {
            return null;
        }
        String actual = product.getProductTypeCodeValueId();
        if (actual == null || actual.isBlank()) {
            return null;
        }
        String actualUpper = actual.trim().toUpperCase();
        if (actualUpper.isBlank()) {
            return null;
        }
        if (desired.contains(actualUpper)) {
            return null;
        }
        return ExclusionReason.of("PURPOSE_NOT_ALLOWED", "대출 목적이 정책상 허용되지 않습니다.");
    }

    private List<ExclusionReason> dedupeReasons(List<ExclusionReason> reasons) {
        if (reasons == null || reasons.isEmpty()) {
            return new ArrayList<>();
        }
        Map<String, ExclusionReason> deduped = new java.util.LinkedHashMap<>();
        for (ExclusionReason reason : reasons) {
            if (reason == null || reason.getMessage() == null) {
                continue;
            }
            String key = reason.getMessage().trim();
            if (key.isBlank()) {
                continue;
            }
            deduped.putIfAbsent(key, reason);
        }
        return new ArrayList<>(deduped.values());
    }

    private String buildReasonText(List<ExclusionReason> reasons) {
        if (reasons == null || reasons.isEmpty()) {
            return "";
        }
        List<String> messages = new ArrayList<>();
        for (ExclusionReason reason : reasons) {
            if (reason == null || reason.getMessage() == null) {
                continue;
            }
            String msg = reason.getMessage().trim();
            if (!msg.isBlank()) {
                messages.add(msg);
            }
        }
        return String.join(", ", messages);
    }

    private List<Long> reorderByPurpose(List<Long> sorted, Map<Long, Boolean> purposeMismatch) {
        if (sorted == null || sorted.isEmpty()) {
            return sorted;
        }
        List<Long> matched = new ArrayList<>();
        List<Long> mismatched = new ArrayList<>();
        for (Long id : sorted) {
            Boolean mismatch = purposeMismatch.get(id);
            if (Boolean.TRUE.equals(mismatch)) {
                mismatched.add(id);
            } else {
                matched.add(id);
            }
        }
        matched.addAll(mismatched);
        return matched;
    }
}
