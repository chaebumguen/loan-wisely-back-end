package com.ccksy.loan.adapter.finance;

import java.time.LocalDate;
import java.util.*;

/**
 * FinanceRequestAdapter
 * - 내부 도메인/유즈케이스 입력을 "외부 금융 API 호출용 요청 형태"로 변환하는 어댑터입니다.
 * - (Version 1) 외부 연동 스펙이 확정되지 않았으므로, "안전한 범용 Request DTO"를 제공합니다.
 * - 변동성이 큰 외부 필드/파라미터는 임의 확정하지 않고, 키-값 형태로 확장 가능한 구조를 유지합니다.
 */
public class FinanceRequestAdapter {

    /**
     * 외부 API에 "상품 목록 조회"를 요청하기 위한 범용 요청 DTO
     * - 외부 API가 REST/GraphQL/CSV 등 어떤 형태라도 수용 가능하도록 Map 기반으로 설계했습니다.
     */
    public static final class ProductListRequest {
        private final String sourceCode; // 예: SA_Code, SB_Code 등 (있다면)
        private final LocalDate asOfDate; // 기준일
        private final Map<String, Object> params; // 확장 파라미터

        private ProductListRequest(String sourceCode, LocalDate asOfDate, Map<String, Object> params) {
            this.sourceCode = sourceCode;
            this.asOfDate = asOfDate;
            this.params = Collections.unmodifiableMap(new LinkedHashMap<>(params));
        }

        public String getSourceCode() {
            return sourceCode;
        }

        public LocalDate getAsOfDate() {
            return asOfDate;
        }

        public Map<String, Object> getParams() {
            return params;
        }

        @Override
        public String toString() {
            return "ProductListRequest{sourceCode=" + sourceCode + ", asOfDate=" + asOfDate + ", params=" + params + "}";
        }
    }

    /**
     * (Version 1) 내부에서 "금융상품 데이터 로딩"에 필요한 최소 입력만 받아 요청으로 변환합니다.
     *
     * @param sourceCode 예: "SA_Code"(은행연합회), "SB_Code"(여신금융협회) 등. 없으면 null 허용.
     * @param asOfDate   기준일. null이면 오늘 날짜로 고정(결정론 목적: 호출 시점 기준으로 값이 달라지지 않게 호출자가 넘기는 것을 권장)
     * @param providerNames 필터(선택). 예: ["한국스탠다드차타드은행", "토스뱅크 주식회사"]
     * @param productNames  필터(선택). 예: ["일반신용대출", "마이너스한도대출"]
     */
    public ProductListRequest buildProductListRequest(
            String sourceCode,
            LocalDate asOfDate,
            List<String> providerNames,
            List<String> productNames
    ) {
        Map<String, Object> params = new LinkedHashMap<>();

        if (providerNames != null && !providerNames.isEmpty()) {
            params.put("providerNames", sanitizeStringList(providerNames));
        }
        if (productNames != null && !productNames.isEmpty()) {
            params.put("productNames", sanitizeStringList(productNames));
        }

        // 외부 연동에서 자주 필요한 "페이지" 파라미터는 기본만 제공 (스펙 확정 전이므로 강제하지 않음)
        params.put("page", 0);
        params.put("size", 100);

        LocalDate fixedAsOf = (asOfDate != null) ? asOfDate : LocalDate.now();
        return new ProductListRequest(nullIfBlank(sourceCode), fixedAsOf, params);
    }

    /**
     * 외부 API에 "단일 기관/단일 상품 상세 조회"가 필요할 때를 대비한 범용 요청 DTO
     */
    public static final class ProductDetailRequest {
        private final String providerName;
        private final String productName;
        private final LocalDate asOfDate;
        private final Map<String, Object> params;

        private ProductDetailRequest(String providerName, String productName, LocalDate asOfDate, Map<String, Object> params) {
            this.providerName = providerName;
            this.productName = productName;
            this.asOfDate = asOfDate;
            this.params = Collections.unmodifiableMap(new LinkedHashMap<>(params));
        }

        public String getProviderName() {
            return providerName;
        }

        public String getProductName() {
            return productName;
        }

        public LocalDate getAsOfDate() {
            return asOfDate;
        }

        public Map<String, Object> getParams() {
            return params;
        }

        @Override
        public String toString() {
            return "ProductDetailRequest{providerName=" + providerName + ", productName=" + productName + ", asOfDate=" + asOfDate + ", params=" + params + "}";
        }
    }

    public ProductDetailRequest buildProductDetailRequest(
            String providerName,
            String productName,
            LocalDate asOfDate
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        // 추후 외부 스펙 확정 시, 식별자/코드 기반 파라미터를 여기에 추가할 수 있습니다.
        LocalDate fixedAsOf = (asOfDate != null) ? asOfDate : LocalDate.now();
        return new ProductDetailRequest(
                requireNonBlank(providerName, "providerName"),
                requireNonBlank(productName, "productName"),
                fixedAsOf,
                params
        );
    }

    // ----------------------
    // Internal helpers
    // ----------------------

    private static List<String> sanitizeStringList(List<String> input) {
        List<String> out = new ArrayList<>();
        for (String s : input) {
            String v = nullIfBlank(s);
            if (v != null) out.add(v);
        }
        return out;
    }

    private static String requireNonBlank(String value, String fieldName) {
        String v = nullIfBlank(value);
        if (v == null) {
            throw new IllegalArgumentException(fieldName + " must not be blank.");
        }
        return v;
    }

    private static String nullIfBlank(String value) {
        if (value == null) return null;
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }
}
