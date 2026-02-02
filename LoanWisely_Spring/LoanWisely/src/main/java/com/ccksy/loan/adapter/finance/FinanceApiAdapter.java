package com.ccksy.loan.adapter.finance;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * FinanceApiAdapter
 * - 외부 금융 API 응답(문자열/JSON 등)을 내부에서 쓰기 쉬운 형태로 변환합니다.
 * - (Version 1) 외부 API 스키마가 확정되지 않았으므로,
 *   1) "문자열 기반" 입력(금리/상환방식 파싱)
 *   2) "JsonNode 기반" 입력(필드명이 달라도 매핑 가능한 안전 접근)
 *
 * 결정론/재현성을 위해:
 * - 파싱 로직은 순수 함수 형태로 유지합니다.
 * - 동일 입력 → 동일 출력이 되도록 구현합니다.
 */
public class FinanceApiAdapter {

    /**
     * 내부에서 추천/비교에 사용할 최소 단위 상품 표현 (Adapter 내부 전용)
     * - 도메인 엔티티로 직접 매핑은 외부 스펙 확정 후 진행하는 것을 권장드립니다.
     */
    public static final class FinanceProductMeta {
        private final String providerName;
        private final String productName;
        private final RateRange rateRange;     // 금리 범위(없으면 null 가능)
        private final String repaymentType;    // 상환방식(원문 유지)
        private final String source;           // 예: KCB, NICE, 공개/내부 등
        private final Map<String, Object> extras; // 확장 정보

        public FinanceProductMeta(
                String providerName,
                String productName,
                RateRange rateRange,
                String repaymentType,
                String source,
                Map<String, Object> extras
        ) {
            this.providerName = requireNonBlank(providerName, "providerName");
            this.productName = requireNonBlank(productName, "productName");
            this.rateRange = rateRange;
            this.repaymentType = (repaymentType == null ? null : repaymentType.trim());
            this.source = (source == null ? null : source.trim());
            this.extras = Collections.unmodifiableMap(new LinkedHashMap<>(extras == null ? Map.of() : extras));
        }

        public String getProviderName() { return providerName; }
        public String getProductName() { return productName; }
        public RateRange getRateRange() { return rateRange; }
        public String getRepaymentType() { return repaymentType; }
        public String getSource() { return source; }
        public Map<String, Object> getExtras() { return extras; }

        @Override
        public String toString() {
            return "FinanceProductMeta{" +
                    "providerName='" + providerName + '\'' +
                    ", productName='" + productName + '\'' +
                    ", rateRange=" + rateRange +
                    ", repaymentType='" + repaymentType + '\'' +
                    ", source='" + source + '\'' +
                    ", extras=" + extras +
                    '}';
        }
    }

    /**
     * 금리 범위 표현
     * - min/max 모두 null 가능 (외부 데이터가 "-"처럼 비어 있는 경우)
     */
    public static final class RateRange {
        private final BigDecimal min;
        private final BigDecimal max;

        private RateRange(BigDecimal min, BigDecimal max) {
            this.min = min;
            this.max = max;
        }

        public BigDecimal getMin() { return min; }
        public BigDecimal getMax() { return max; }

        @Override
        public String toString() {
            return "RateRange{min=" + min + ", max=" + max + '}';
        }
    }

    /**
     * 예: "5.04~6.31 %", "5.19~12.28 %", "-", "은행별 금리에 따름"
     * - 파싱 불가/미제공이면 null 반환 (판단 입력에서 제외 가능)
     */
    public RateRange parseRateRangeOrNull(String rawRateText) {
        String s = nullIfBlank(rawRateText);
        if (s == null) return null;

        // 명백히 수치가 아닌 케이스들
        String lowered = s.toLowerCase(Locale.ROOT);
        if (s.equals("-") || lowered.contains("따름") || lowered.contains("별") && lowered.contains("금리")) {
            return null;
        }

        // 공백/퍼센트 제거
        String cleaned = s.replace("%", "").replace("％", "").trim();

        // "~" 범위
        if (cleaned.contains("~")) {
            String[] parts = cleaned.split("~");
            if (parts.length != 2) return null;

            BigDecimal min = parseDecimalOrNull(parts[0]);
            BigDecimal max = parseDecimalOrNull(parts[1]);

            // 둘 다 없으면 무의미
            if (min == null && max == null) return null;

            // 한쪽만 있으면 그 값만 세팅
            // 둘 다 있으면 min<=max 보정
            if (min != null && max != null && min.compareTo(max) > 0) {
                BigDecimal tmp = min;
                min = max;
                max = tmp;
            }
            return new RateRange(min, max);
        }

        // 단일 값(예: "15.90")
        BigDecimal single = parseDecimalOrNull(cleaned);
        if (single == null) return null;
        return new RateRange(single, single);
    }

    /**
     * 외부 응답이 "표 형태"로 들어온 경우(예: CSV, 엑셀 파싱 결과) 한 행을 상품 메타로 변환
     */
    public FinanceProductMeta fromRow(
            String providerName,
            String productName,
            String rateText,
            String repaymentType,
            String source
    ) {
        RateRange range = parseRateRangeOrNull(rateText);
        return new FinanceProductMeta(providerName, productName, range, repaymentType, source, Map.of(
                "rawRateText", nullIfBlank(rateText)
        ));
    }

    /**
     * 외부 응답이 JSON 형태로 들어온 경우를 위한 안전 매핑
     * - 필드명이 외부마다 다를 수 있으므로 후보 키 배열을 받아 순서대로 탐색합니다.
     */
    public FinanceProductMeta fromJsonNode(
            JsonNode node,
            String[] providerNameKeys,
            String[] productNameKeys,
            String[] rateKeys,
            String[] repaymentKeys,
            String[] sourceKeys
    ) {
        Objects.requireNonNull(node, "node must not be null");

        String provider = firstText(node, providerNameKeys);
        String product = firstText(node, productNameKeys);
        String rateText = firstText(node, rateKeys);
        String repayment = firstText(node, repaymentKeys);
        String source = firstText(node, sourceKeys);

        RateRange range = parseRateRangeOrNull(rateText);

        Map<String, Object> extras = new LinkedHashMap<>();
        extras.put("rawRateText", nullIfBlank(rateText));

        // JSON 원문 일부를 보관하고 싶다면 여기서 path 기록 방식으로 확장 가능합니다(Version 1에서는 원문 저장 강제 안 함)
        return new FinanceProductMeta(provider, product, range, repayment, source, extras);
    }

    /**
     * JSON 필드 키 후보를 순서대로 찾아 첫 번째 텍스트를 반환합니다.
     */
    private static String firstText(JsonNode node, String[] keys) {
        if (keys == null || keys.length == 0) return null;

        for (String k : keys) {
            if (k == null || k.isBlank()) continue;
            JsonNode v = node.get(k);
            if (v == null || v.isNull()) continue;
            String t = v.asText();
            if (t != null && !t.trim().isEmpty()) return t.trim();
        }
        return null;
    }

    private static BigDecimal parseDecimalOrNull(String raw) {
        String s = nullIfBlank(raw);
        if (s == null) return null;

        // 숫자/소수점/부호 외 제거 (예: "6.31 " 등)
        String normalized = s.replaceAll("[^0-9.\\-+]", "");
        if (normalized.isBlank()) return null;

        try {
            BigDecimal d = new BigDecimal(normalized);
            // 금리는 보통 소수점 2자리 정도면 충분 (표현 안정화)
            return d.setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static String requireNonBlank(String value, String fieldName) {
        String v = nullIfBlank(value);
        if (v == null) throw new IllegalArgumentException(fieldName + " must not be blank.");
        return v;
    }

    private static String nullIfBlank(String value) {
        if (value == null) return null;
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }
}
