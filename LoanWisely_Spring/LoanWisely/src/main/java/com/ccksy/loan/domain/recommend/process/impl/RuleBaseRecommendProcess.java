// FILE: domain/recommend/process/impl/RuleBasedRecommendProcess.java
package com.ccksy.loan.domain.recommend.process.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.ccksy.loan.domain.recommend.dto.internal.RecommendContext;
import com.ccksy.loan.domain.recommend.filter.chain.ChainFactory;
import com.ccksy.loan.domain.recommend.filter.model.FilterContext;
import com.ccksy.loan.domain.recommend.policy.scoring.CompositeScoreStrategy;
import com.ccksy.loan.domain.recommend.policy.scoring.PurposeScoreStrategy;
import com.ccksy.loan.domain.recommend.policy.scoring.RiskScoreStrategy;
import com.ccksy.loan.domain.recommend.policy.scoring.ScoreStrategy;
import com.ccksy.loan.domain.recommend.process.template.AbstractRecommendProcess;
import com.ccksy.loan.domain.recommend.result.core.RecommendItem;

/**
 * 룰 기반 추천 프로세스 구현 (v1)
 *
 * - Template Method: validate -> filter -> score -> sort
 * - filter 단계: ChainFactory(정책버전 기반 고정 순서)로 후보 제외
 * - score 단계: Strategy 조합(Composite)으로 점수 부여(결정론)
 * - sort 단계: score 내림차순 정렬(동점은 입력 순서 유지)
 */
@Component
public class RuleBaseRecommendProcess extends AbstractRecommendProcess {

    private final ChainFactory chainFactory;

    public RuleBaseRecommendProcess(ChainFactory chainFactory) {
        this.chainFactory = Objects.requireNonNull(chainFactory, "chainFactory");
    }

    @Override
    protected void validate(RecommendContext context) {
        Objects.requireNonNull(context, "context");

        // v1 최소 검증: 승인본 버전 문자열은 공백 불가 (RecommendContext 생성 단계에서도 검증되지만 이중 방어)
        requireText(context.getPolicyVersion(), "policyVersion");
        requireText(context.getCreditMetaVersion(), "creditMetaVersion");
        requireText(context.getFinancialMetaVersion(), "financialMetaVersion");
    }

    @Override
    protected List<RecommendItem> filter(RecommendContext context, List<RecommendItem> candidates) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(candidates, "candidates");

        List<RecommendItem> kept = new ArrayList<>(candidates.size());

        for (RecommendItem item : candidates) {
            if (item == null) continue;

            // RecommendItem -> candidate map (표준키 고정: FilterContext.CandidateKeys)
            Map<String, Object> candidateMap = toCandidateMap(item);

            // Fail-Fast는 "체인 내부"에서 적용(첫 실패 사유 반환)
            String reasonCode = chainFactory.evaluate(context, candidateMap);

            if (reasonCode == null) {
                kept.add(item);
            } else {
                // 제외 사유를 item에 기록 시도(필드/세터 존재 시에만)
                trySetText(item, "setExclusionReasonCode", reasonCode);
                trySetText(item, "setExcludeReasonCode", reasonCode);
                trySetText(item, "setExclusionCode", reasonCode);
            }
        }

        return kept;
    }

    @Override
    protected void score(RecommendContext context, List<RecommendItem> items) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(items, "items");

        // v1 결정론: 전략/가중치/순서 고정
        ScoreStrategy composite = new CompositeScoreStrategy(List.of(
                new CompositeScoreStrategy.Entry(new PurposeScoreStrategy(), 0.40d),
                new CompositeScoreStrategy.Entry(new RiskScoreStrategy(), 0.60d)
        ));

        composite.apply(context, items);
    }

    @Override
    protected void sort(List<RecommendItem> items) {
        Objects.requireNonNull(items, "items");

        // v1: score 내림차순, 동점은 안정 정렬(입력 순서 유지)
        // Java의 List.sort는 TimSort(안정 정렬)이므로 Comparator가 0이면 입력 순서 유지.
        items.sort((a, b) -> {
            double sb = readScore(b);
            double sa = readScore(a);
            return Double.compare(sb, sa);
        });
    }

    // -------------------------
    // 내부 유틸 (추가 파일 생성 금지)
    // -------------------------

    private Map<String, Object> toCandidateMap(RecommendItem item) {
        Map<String, Object> m = new HashMap<>();

        // 표준키는 FilterContext.CandidateKeys에 고정
        putIfNotNull(m, FilterContext.CandidateKeys.PRODUCT_ID,
                tryInvoke(item, "getProductId", "getId", "getProductCode"));

        putIfNotNull(m, FilterContext.CandidateKeys.PROVIDER_CODE,
                tryInvoke(item, "getProviderCode", "getProvider", "getSourceCode"));

        putIfNotNull(m, FilterContext.CandidateKeys.PRODUCT_TYPE_CODE,
                tryInvoke(item, "getProductTypeCode", "getProductType", "getTypeCode"));

        putIfNotNull(m, FilterContext.CandidateKeys.MIN_CREDIT_SCORE,
                tryInvoke(item, "getMinCreditScore", "getRequiredCreditScore"));

        putIfNotNull(m, FilterContext.CandidateKeys.MAX_DSR,
                tryInvoke(item, "getMaxDsr", "getDsrLimit"));

        putIfNotNull(m, FilterContext.CandidateKeys.ELIGIBLE,
                tryInvoke(item, "getEligible", "isEligible"));

        putIfNotNull(m, FilterContext.CandidateKeys.INELIGIBLE_REASON_CODE,
                tryInvoke(item, "getIneligibleReasonCode", "getIneligibleReason", "getRejectReasonCode"));

        putIfNotNull(m, FilterContext.CandidateKeys.ALLOWED_PURPOSES,
                tryInvoke(item, "getAllowedPurposes", "getPurposeAllowList"));

        return m;
    }

    private static void putIfNotNull(Map<String, Object> m, String key, Object value) {
        if (value != null) m.put(key, value);
    }

    private static Object tryInvoke(Object target, String... methodNames) {
        if (target == null || methodNames == null) return null;
        for (String name : methodNames) {
            if (name == null || name.isBlank()) continue;
            try {
                Method method = target.getClass().getMethod(name);
                return method.invoke(target);
            } catch (Exception ignored) {
                // try next
            }
        }
        return null;
    }

    private static void trySetText(Object target, String setterName, String value) {
        if (target == null || setterName == null || setterName.isBlank()) return;
        try {
            Method m = target.getClass().getMethod(setterName, String.class);
            m.invoke(target, value);
        } catch (Exception ignored) {
            // no-op
        }
    }

    private static double readScore(Object item) {
        if (item == null) return 0.0d;

        // getScore() 우선
        Object v = tryInvoke(item, "getScore", "score");
        if (v instanceof Number) return ((Number) v).doubleValue();

        // score 필드 접근 대체는 v1에서 하지 않음(리플렉션 부작용 최소화)
        return 0.0d;
    }

    private static String requireText(String v, String field) {
        if (v == null || v.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return v.trim();
    }
}
