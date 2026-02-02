// FILE: domain/recommend/filter/model/FilterContext.java
package com.ccksy.loan.domain.recommend.filter.model;

import com.ccksy.loan.domain.recommend.dto.internal.RecommendContext;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * 필터 입력 묶음(v1).
 *
 * 폴더 구조 기준 책임:
 * - filter/chain 은 "필터 순서/평가"만 담당
 * - filter/model 은 "필터가 평가할 입력 계약"을 담당 (FilterContext)
 *
 * v1 규칙:
 * - 판단/점수 로직 없음 (데이터 운반 + 안전한 조회만)
 * - 후보(candidate)는 표준 모델 확정 전까지 Map으로 받되, 키는 이 컨텍스트가 표준화한다.
 */
public final class FilterContext {

    /**
     * v1 표준 후보 키(고정).
     * - filter/chain 의 필터들은 candidate Map을 직접 임의 키로 접근하지 않고,
     *   반드시 이 키 정의를 사용해야 한다.
     */
    public static final class CandidateKeys {
        private CandidateKeys() {}

        public static final String PRODUCT_ID = "productId";
        public static final String PROVIDER_CODE = "providerCode";
        public static final String PRODUCT_TYPE_CODE = "productTypeCode";

        public static final String MIN_CREDIT_SCORE = "minCreditScore";
        public static final String MAX_DSR = "maxDsr";

        public static final String ELIGIBLE = "eligible";
        public static final String INELIGIBLE_REASON_CODE = "ineligibleReasonCode";

        public static final String ALLOWED_PURPOSES = "allowedPurposes";
    }

    private final RecommendContext recommendContext;
    private final Map<String, Object> candidate;

    public FilterContext(RecommendContext recommendContext, Map<String, Object> candidate) {
        this.recommendContext = Objects.requireNonNull(recommendContext, "recommendContext");
        this.candidate = Collections.unmodifiableMap(candidate == null ? Collections.emptyMap() : candidate);
    }

    public RecommendContext getRecommendContext() {
        return recommendContext;
    }

    public Map<String, Object> getCandidate() {
        return candidate;
    }

    // ----------------------------
    // Candidate(상품 후보) 조회 헬퍼
    // ----------------------------

    public String candidateText(String key) {
        Object v = candidate.get(key);
        if (v == null) return null;
        String s = String.valueOf(v).trim();
        return s.isEmpty() ? null : s;
    }

    public Long candidateLong(String key) {
        return asLong(candidate.get(key));
    }

    public BigDecimal candidateDecimal(String key) {
        return asDecimal(candidate.get(key));
    }

    public Boolean candidateBoolean(String key) {
        return asBoolean(candidate.get(key));
    }

    // ----------------------------
    // User Options(사용자 입력 옵션) 조회 헬퍼
    // ----------------------------

    public String optionText(String key) {
        Object v = recommendContext.getOptions().get(key);
        if (v == null) return null;
        String s = String.valueOf(v).trim();
        return s.isEmpty() ? null : s;
    }

    public Long optionLong(String key) {
        return asLong(recommendContext.getOptions().get(key));
    }

    public BigDecimal optionDecimal(String key) {
        return asDecimal(recommendContext.getOptions().get(key));
    }

    public Boolean optionBoolean(String key) {
        return asBoolean(recommendContext.getOptions().get(key));
    }

    // ----------------------------
    // 최소 파서(추가 파일 생성 금지에 따라 내부 포함)
    // ----------------------------

    private static Long asLong(Object v) {
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).longValue();
        try {
            String s = String.valueOf(v).trim();
            if (s.isEmpty()) return null;
            return Long.parseLong(s);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static BigDecimal asDecimal(Object v) {
        if (v == null) return null;
        if (v instanceof BigDecimal) return (BigDecimal) v;
        if (v instanceof Number) return BigDecimal.valueOf(((Number) v).doubleValue());
        try {
            String s = String.valueOf(v).trim();
            if (s.isEmpty()) return null;
            return new BigDecimal(s);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Boolean asBoolean(Object v) {
        if (v == null) return null;
        if (v instanceof Boolean) return (Boolean) v;
        String s = String.valueOf(v).trim().toLowerCase();
        if (s.isEmpty()) return null;
        if ("true".equals(s) || "y".equals(s) || "yes".equals(s) || "1".equals(s)) return Boolean.TRUE;
        if ("false".equals(s) || "n".equals(s) || "no".equals(s) || "0".equals(s)) return Boolean.FALSE;
        return null;
    }
}
