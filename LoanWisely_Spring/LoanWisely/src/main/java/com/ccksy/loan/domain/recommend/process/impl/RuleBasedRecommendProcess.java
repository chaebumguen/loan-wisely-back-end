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
 * v1: 猷?湲곕컲 異붿쿇 ?꾨줈?몄뒪(怨듦꺽)
 * - ?ㅼ젣 濡쒕뵫(UserProfile/UserConsent/Product 議고쉶/濡쒕뵫/?뺣젹)? ?ㅼ쓬 PART?먯꽌 ?곌껐
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

        // v1 理쒖냼 FilterContext(?ㅼ젣 媛믪? ?ㅼ쓬 PART?먯꽌 濡쒕뵫/feature濡?梨꾩?)
        RiskScoreResult risk = riskScoringService.score(command.getUserId(), command.getRequestedInputLevel());

        FilterContext filterContext = new FilterContext(
                command.getUserId(),
                command.getRequestedInputLevel(),
                risk.getCreditScore(),
                risk.getDsr(),
                risk.getLoanPurposeCode(),
                LocalDateTime.now()
        );

        boolean notReady = false;
        List<ExclusionReason> warnings = new ArrayList<>();
        if (!eligibilityPolicy.isEligible(filterContext)) {
            notReady = true;
            warnings.add(ExclusionReason.of("ELIGIBILITY_FAILED", "추천 기본 조건을 충족하지 않습니다."));
        }

        // ?꾪꽣 泥댁씤(?꾩옱 湲곕낯 泥댁씤: creditScore/dsr媛 null?대㈃ ?쒖쇅 ?ъ쑀 諛쒖깮)
        IneligibilityFilter chain = ChainFactory.createDefaultChain();
        Optional<ExclusionReason> reason = chain.check(filterContext);

        if (reason.isPresent()) {
            notReady = true;
            warnings.add(reason.get());
        }

        // TODO: ?곹뭹 濡쒕뵫 + 議곌굔 濡쒖쭅 + ?뺣젹 + ?쒖쇅?ъ쑀(productId蹂?
        List<LoanProduct> candidates = loadCandidates(filterContext);
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

            scores.put(productId, totalScore);
            rateMins.put(productId, rateMin);
        }

        List<Long> sorted = sortStrategy.sort(productIds, scores, rateMins);
        List<RecommendItem> items = new ArrayList<>();
        for (int i = 0; i < sorted.size() && i < MAX_RECOMMEND_ITEMS; i++) {
            Long productId = sorted.get(i);
            BigDecimal score = scores.getOrDefault(productId, BigDecimal.ZERO);
            BigDecimal rateMin = rateMins.get(productId);
            String reasonText = "score=" + score + ", rateMin=" + rateMin;
            items.add(RecommendItemBuilder.of(productId, score, rateMin, reasonText));
        }

        var b = (notReady
                ? RecommendResultBuilder.notReady(command.getReproduceKey(), new NotReadyState().code())
                : RecommendResultBuilder.ready(command.getReproduceKey(), new ReadyState().code()))
                .resolvedInputLevel(command.getRequestedInputLevel())
                .items(items);

        for (ExclusionReason warn : warnings) {
            b = RecommendResultBuilder.addGlobalWarning(b, warn.getCode(), warn.getMessage());
        }

        return b.build();
    }

    private List<LoanProduct> loadCandidates(FilterContext filterContext) {
        String productType = resolveProductType(filterContext.getLoanPurposeCode());
        List<LoanProduct> list = loanProductMapper.selectList(null, productType, null, null);
        if (list != null && !list.isEmpty()) {
            return list;
        }
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

    private String resolveProductType(String purposeCode) {
        if (purposeCode == null || purposeCode.isBlank()) {
            return null;
        }
        String v = purposeCode.toUpperCase();
        if (v.contains("RENT") || v.contains("JEONSE") || v.contains("?꾩꽭") || v.contains("?붿꽭")) {
            return "RENT";
        }
        if (v.contains("MORTGAGE") || v.contains("二쇳깮") || v.contains("二쇰떞") || v.contains("?대낫")) {
            return "MORTGAGE";
        }
        if (v.contains("CREDIT") || v.contains("?좎슜")) {
            return "CREDIT";
        }
        if (v.equals("CREDIT") || v.equals("MORTGAGE") || v.equals("RENT")) {
            return v;
        }
        return null;
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
}


