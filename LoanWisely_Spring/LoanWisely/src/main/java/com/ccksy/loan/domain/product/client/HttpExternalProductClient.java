package com.ccksy.loan.domain.product.client;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpExternalProductClient implements ExternalProductClient {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .build();

    @Value("${product.external.fss.credit-url:}")
    private String creditUrl;

    @Value("${product.external.fss.mortgage-url:}")
    private String mortgageUrl;

    @Value("${product.external.fss.rent-url:}")
    private String rentUrl;

    @Value("${product.external.fss.api-key:}")
    private String apiKey;

    @Value("${product.external.fss.api-key-param:auth}")
    private String apiKeyParam;

    @Value("${product.external.fss.response-type:json}")
    private String responseType;

    @Value("${product.external.fss.top-fin-grp-nos:}")
    private String topFinGrpNos;

    @Value("${product.external.fss.top-fin-grp-nos-credit:}")
    private String topFinGrpNosCredit;

    @Value("${product.external.fss.top-fin-grp-nos-mortgage:}")
    private String topFinGrpNosMortgage;

    @Value("${product.external.fss.top-fin-grp-nos-rent:}")
    private String topFinGrpNosRent;

    @Value("${product.external.fss.page-no:1}")
    private String pageNo;

    @Value("${product.external.fss.finance-cd:}")
    private String financeCd;

    @Override
    public List<ExternalLoanProductDto> fetchCreditProducts() {
        if (creditUrl == null || creditUrl.isBlank()) {
            return Collections.emptyList();
        }
        try {
            List<String> groupNos = resolveGroupNos(topFinGrpNosCredit, topFinGrpNos);
            List<ExternalLoanProductDto> out = new ArrayList<>();
            for (String groupNo : groupNos) {
                String url = buildFssUrl(creditUrl, groupNo);
                HttpRequest.Builder builder = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(TIMEOUT)
                        .GET()
                        .header("Accept", "application/json")
                        .header("User-Agent", "LoanWisely/1.0");

                HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() / 100 != 2) {
                    log.warn("FSS credit API non-2xx status: {} groupNo={} body={}", response.statusCode(), groupNo, safeBody(response.body()));
                    continue;
                }

                String contentType = response.headers().firstValue("Content-Type").orElse("");
                if (!contentType.toLowerCase().contains("json")) {
                    log.warn("FSS credit API non-JSON response. groupNo={} contentType={} body={}", groupNo, contentType, safeBody(response.body()));
                    continue;
                }

                FssCreditResponse fssResponse = objectMapper.readValue(response.body(), FssCreditResponse.class);
                if (fssResponse == null || fssResponse.result == null || fssResponse.result.baseList == null) {
                    continue;
                }
                if (fssResponse.result.err_cd != null && !"000".equals(fssResponse.result.err_cd)) {
                    log.warn("FSS credit API error. groupNo={} err_cd={} err_msg={}", groupNo, fssResponse.result.err_cd, fssResponse.result.err_msg);
                    continue;
                }
                log.info("FSS credit API ok. groupNo={} baseListSize={} optionListSize={}",
                        groupNo,
                        fssResponse.result.baseList.size(),
                        fssResponse.result.optionList == null ? 0 : fssResponse.result.optionList.size());

                Map<String, List<FssCreditOption>> optionsByKey = groupOptions(fssResponse.result.optionList);
                List<ExternalLoanProductDto> items = fssResponse.result.baseList.stream()
                        .map(base -> toExternalDto(base, optionsByKey.getOrDefault(keyOf(base.fin_co_no, base.fin_prdt_cd), Collections.emptyList())))
                        .collect(Collectors.toList());
                out.addAll(items);
            }
            return out;
        } catch (Exception e) {
            log.warn("FSS credit API request failed.", e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "External product API request failed.");
        }
    }

    @Override
    public List<ExternalLoanProductDto> fetchMortgageProducts() {
        if (mortgageUrl == null || mortgageUrl.isBlank()) {
            return Collections.emptyList();
        }
        return fetchGenericProducts(mortgageUrl, topFinGrpNosMortgage, "MORTGAGE");
    }

    @Override
    public List<ExternalLoanProductDto> fetchRentProducts() {
        if (rentUrl == null || rentUrl.isBlank()) {
            return Collections.emptyList();
        }
        return fetchGenericProducts(rentUrl, topFinGrpNosRent, "RENT");
    }

    private List<ExternalLoanProductDto> fetchGenericProducts(String baseUrl, String topFinGrpNosSpecific, String productType) {
        try {
            List<ExternalLoanProductDto> out = new ArrayList<>();
            List<String> groupNos = resolveGroupNos(topFinGrpNosSpecific, topFinGrpNos);
            for (String groupNo : groupNos) {
                String url = buildFssUrl(baseUrl, groupNo);
                HttpRequest.Builder builder = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(TIMEOUT)
                        .GET()
                        .header("Accept", "application/json")
                        .header("User-Agent", "LoanWisely/1.0");

                HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() / 100 != 2) {
                    log.warn("FSS {} API non-2xx status: {} groupNo={} body={}", productType, response.statusCode(), groupNo, safeBody(response.body()));
                    continue;
                }

                String contentType = response.headers().firstValue("Content-Type").orElse("");
                if (!contentType.toLowerCase().contains("json")) {
                    log.warn("FSS {} API non-JSON response. groupNo={} contentType={} body={}", productType, groupNo, contentType, safeBody(response.body()));
                    continue;
                }

                com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(response.body());
                com.fasterxml.jackson.databind.JsonNode result = root.path("result");
                if (result.isMissingNode()) {
                    continue;
                }
                String errCd = result.path("err_cd").asText();
                if (!"000".equals(errCd)) {
                    log.warn("FSS {} API error. groupNo={} err_cd={} err_msg={}", productType, groupNo, errCd, result.path("err_msg").asText());
                    continue;
                }
                com.fasterxml.jackson.databind.JsonNode baseList = result.path("baseList");
                com.fasterxml.jackson.databind.JsonNode optionList = result.path("optionList");

                Map<String, List<com.fasterxml.jackson.databind.JsonNode>> optionsByKey = groupOptions(optionList);

                if (baseList.isArray()) {
                    for (com.fasterxml.jackson.databind.JsonNode base : baseList) {
                        ExternalLoanProductDto dto = new ExternalLoanProductDto();
                        dto.setProductTypeCodeValueId(productType);
                        dto.setProductName(base.path("fin_prdt_nm").asText(null));
                        dto.setCompanyName(base.path("kor_co_nm").asText(null));
                        dto.setFinCoNo(base.path("fin_co_no").asText(null));
                        dto.setFinPrdtCd(base.path("fin_prdt_cd").asText(null));
                        dto.setJoinWay(base.path("join_way").asText(null));
                        dto.setNote(dto.getJoinWay());
                        dto.setCbName(base.path("cb_name").asText(null));
                        dto.setEndDate(parseFssDate(base.path("dcls_end_day").asText(null)));
                        dto.setAsOfDate(parseFssMonth(base.path("dcls_month").asText(null)));

                        String key = keyOf(dto.getFinCoNo(), dto.getFinPrdtCd());
                        List<com.fasterxml.jackson.databind.JsonNode> optionNodes = optionsByKey.getOrDefault(key, Collections.emptyList());
                        RateStats stats = computeRateStatsGeneric(optionNodes);
                        if (stats != null) {
                            dto.setRateMin(stats.min);
                            dto.setRateMax(stats.max);
                            dto.setRateBase(stats.base);
                            dto.setRateTypeName(stats.rateTypeName);
                            dto.setRateTypeCodeValueId(stats.rateTypeName);
                        }
                        if (!optionNodes.isEmpty()) {
                            com.fasterxml.jackson.databind.JsonNode opt = optionNodes.get(0);
                            String rpayType = opt.path("rpay_type").asText(null);
                            if (rpayType != null && !rpayType.isBlank()) {
                                dto.setRepaymentTypeCodeValueId("RPAY_TYPE_" + rpayType);
                            } else {
                                dto.setRepaymentTypeCodeValueId("UNKNOWN");
                            }
                            String mrtgType = opt.path("mrtg_type").asText(null);
                            if (mrtgType != null && !mrtgType.isBlank()) {
                                dto.setCollateralTypeCodeValueId("MRTG_TYPE_" + mrtgType);
                                dto.setLoanTypeCodeValueId("MRTG_TYPE_" + mrtgType);
                            } else {
                                dto.setLoanTypeCodeValueId("LOAN_TYPE_" + productType);
                            }

                            String rateTypeCode = pickText(opt, "lend_rate_type", "intr_rate_type");
                            if (rateTypeCode != null && !rateTypeCode.isBlank()) {
                                dto.setRateTypeCodeValueId("RATE_TYPE_" + rateTypeCode);
                            }
                        } else {
                            dto.setRepaymentTypeCodeValueId("UNKNOWN");
                            dto.setLoanTypeCodeValueId("UNKNOWN");
                        }
                        out.add(dto);
                    }
                }
                log.info("FSS {} API ok. groupNo={} baseListSize={} optionListSize={}", productType,
                        groupNo,
                        baseList.isArray() ? baseList.size() : 0,
                        optionList.isArray() ? optionList.size() : 0);
            }
            return out;
        } catch (Exception e) {
            log.warn("FSS {} API request failed.", productType, e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "External product API request failed.");
        }
    }

    private String buildFssUrl(String baseUrl, String topFinGrpNo) {
        StringBuilder sb = new StringBuilder(baseUrl);
        String queryPrefix = baseUrl.contains("?") ? "&" : "?";
        sb.append(queryPrefix);
        sb.append(encodeParam(apiKeyParam, apiKey));

        if (responseType != null && !responseType.isBlank()) {
            sb.append("&").append(encodeParam("responseType", responseType));
        }

        if (topFinGrpNo != null && !topFinGrpNo.isBlank()) {
            sb.append("&").append(encodeParam("topFinGrpNo", topFinGrpNo));
        }

        if (pageNo != null && !pageNo.isBlank()) {
            sb.append("&").append(encodeParam("pageNo", pageNo));
        }

        if (financeCd != null && !financeCd.isBlank()) {
            sb.append("&").append(encodeParam("financeCd", financeCd));
        }

        return sb.toString();
    }

    private List<String> resolveGroupNos(String specific, String fallback) {
        String raw = (specific != null && !specific.isBlank()) ? specific : fallback;
        if (raw == null || raw.isBlank()) {
            return List.of("");
        }
        return java.util.Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
    }

    private String encodeParam(String key, String value) {
        return URLEncoder.encode(key, StandardCharsets.UTF_8) + "="
                + URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private ExternalLoanProductDto toExternalDto(FssCreditBase base, List<FssCreditOption> options) {
        ExternalLoanProductDto dto = new ExternalLoanProductDto();
        dto.setProductName(base.fin_prdt_nm);
        dto.setProductTypeCodeValueId("CREDIT");
        dto.setLoanTypeCodeValueId(base.crdt_prdt_type == null ? "CRDT_TYPE_UNKNOWN" : "CRDT_TYPE_" + base.crdt_prdt_type);
        dto.setRepaymentTypeCodeValueId("UNKNOWN");
        dto.setCompanyName(base.kor_co_nm);
        dto.setFinCoNo(base.fin_co_no);
        dto.setFinPrdtCd(base.fin_prdt_cd);
        dto.setJoinWay(base.join_way);
        dto.setLoanInciExpn(base.loan_inci_expn);
        dto.setCbName(base.cb_name);
        dto.setProductDetailType(base.crdt_prdt_type);
        dto.setProductDetailTypeName(base.crdt_prdt_type_nm);
        dto.setNote(base.join_way != null && !base.join_way.isBlank() ? base.join_way : base.loan_inci_expn);
        dto.setEndDate(parseFssDate(base.dcls_end_day));
        dto.setAsOfDate(parseFssMonth(base.dcls_month));

        RateStats stats = computeRateStats(options);
        if (stats != null) {
            dto.setRateMin(stats.min);
            dto.setRateMax(stats.max);
            dto.setRateBase(stats.base);
            dto.setRateTypeName(stats.rateTypeName);
            dto.setRateTypeCodeValueId(stats.rateTypeName);
        }
        return dto;
    }

    private LocalDate parseFssDate(String yyyymmdd) {
        if (yyyymmdd == null || yyyymmdd.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(yyyymmdd, DateTimeFormatter.BASIC_ISO_DATE);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private LocalDate parseFssMonth(String yyyymm) {
        if (yyyymm == null || yyyymm.isBlank()) {
            return null;
        }
        if (yyyymm.length() != 6) {
            return null;
        }
        return parseFssDate(yyyymm + "01");
    }

    private Map<String, List<FssCreditOption>> groupOptions(List<FssCreditOption> options) {
        if (options == null || options.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, List<FssCreditOption>> map = new HashMap<>();
        for (FssCreditOption opt : options) {
            String key = keyOf(opt.fin_co_no, opt.fin_prdt_cd);
            if (key == null) {
                continue;
            }
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(opt);
        }
        return map;
    }

    private Map<String, List<com.fasterxml.jackson.databind.JsonNode>> groupOptions(com.fasterxml.jackson.databind.JsonNode optionList) {
        if (optionList == null || !optionList.isArray()) {
            return Collections.emptyMap();
        }
        Map<String, List<com.fasterxml.jackson.databind.JsonNode>> map = new HashMap<>();
        for (com.fasterxml.jackson.databind.JsonNode opt : optionList) {
            String finCoNo = opt.path("fin_co_no").asText(null);
            String finPrdtCd = opt.path("fin_prdt_cd").asText(null);
            String key = keyOf(finCoNo, finPrdtCd);
            if (key == null) {
                continue;
            }
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(opt);
        }
        return map;
    }

    private String keyOf(String finCoNo, String finPrdtCd) {
        if (finCoNo == null || finPrdtCd == null) {
            return null;
        }
        return finCoNo + "|" + finPrdtCd;
    }

    private RateStats computeRateStats(List<FssCreditOption> options) {
        if (options == null || options.isEmpty()) {
            return null;
        }
        Map<String, RateStats> byType = new HashMap<>();
        for (FssCreditOption opt : options) {
            String typeCode = opt.crdt_lend_rate_type;
            String typeName = opt.crdt_lend_rate_type_nm;
            RateStats stats = byType.computeIfAbsent(
                    typeCode == null ? "" : typeCode.trim().toUpperCase(),
                    k -> new RateStats(null, null, null, typeName)
            );
            for (String val : opt.gradeRates()) {
                BigDecimal v = parseDecimal(val);
                if (v == null) continue;
                if (stats.min == null || v.compareTo(stats.min) < 0) stats.min = v;
                if (stats.max == null || v.compareTo(stats.max) > 0) stats.max = v;
            }
            if (stats.rateTypeName == null && typeName != null && !typeName.isBlank()) {
                stats.rateTypeName = typeName;
            }
        }

        RateStats loanRate = byType.get("A");
        if (loanRate != null && (loanRate.min != null || loanRate.max != null)) {
            loanRate.rateTypeName = loanRate.rateTypeName == null ? "대출금리" : loanRate.rateTypeName;
            return loanRate;
        }

        RateStats base = byType.get("B");
        RateStats add = byType.get("C");
        RateStats adj = byType.get("D");
        if (base == null || add == null) {
            return null;
        }
        BigDecimal min = null;
        BigDecimal max = null;
        if (base.min != null && add.min != null) {
            BigDecimal dMax = adj == null ? BigDecimal.ZERO : (adj.max == null ? BigDecimal.ZERO : adj.max);
            min = base.min.add(add.min).subtract(dMax);
        }
        if (base.max != null && add.max != null) {
            BigDecimal dMin = adj == null ? BigDecimal.ZERO : (adj.min == null ? BigDecimal.ZERO : adj.min);
            max = base.max.add(add.max).subtract(dMin);
        }
        if (min == null && max == null) {
            return null;
        }
        return new RateStats(min, max, null, "대출금리");
    }

    private RateStats computeRateStatsGeneric(List<com.fasterxml.jackson.databind.JsonNode> options) {
        if (options == null || options.isEmpty()) {
            return null;
        }
        Map<String, RateStats> byType = new HashMap<>();
        for (com.fasterxml.jackson.databind.JsonNode opt : options) {
            String typeCode = pickText(opt, "lend_rate_type", "intr_rate_type", "rate_type");
            String typeName = pickText(opt, "lend_rate_type_nm", "intr_rate_type_nm", "rate_type_nm");
            String key = typeCode == null ? "" : typeCode.trim().toUpperCase();
            RateStats stats = byType.computeIfAbsent(key, k -> new RateStats(null, null, null, typeName));

            BigDecimal vMin = pickDecimal(opt, "lend_rate_min", "intr_rate_min", "rate_min", "rent_rate_min", "rent_rate");
            BigDecimal vMax = pickDecimal(opt, "lend_rate_max", "intr_rate_max", "rate_max", "rent_rate_max", "rent_rate");
            BigDecimal vBase = pickDecimal(opt, "lend_rate_avg", "intr_rate", "rate_avg", "rate_base", "rent_rate_avg", "rent_rate");
            if (vMin != null) {
                stats.min = (stats.min == null || vMin.compareTo(stats.min) < 0) ? vMin : stats.min;
            }
            if (vMax != null) {
                stats.max = (stats.max == null || vMax.compareTo(stats.max) > 0) ? vMax : stats.max;
            }
            if (stats.base == null && vBase != null) {
                stats.base = vBase;
            }
            if (stats.rateTypeName == null && typeName != null && !typeName.isBlank()) {
                stats.rateTypeName = typeName;
            }
        }

        RateStats loanRate = byType.get("A");
        if (loanRate != null && (loanRate.min != null || loanRate.max != null || loanRate.base != null)) {
            loanRate.rateTypeName = loanRate.rateTypeName == null ? "대출금리" : loanRate.rateTypeName;
            return loanRate;
        }

        RateStats base = byType.get("B");
        RateStats add = byType.get("C");
        RateStats adj = byType.get("D");
        if (base == null || add == null) {
            return null;
        }
        BigDecimal min = null;
        BigDecimal max = null;
        BigDecimal baseRate = null;
        if (base.min != null && add.min != null) {
            BigDecimal dMax = adj == null ? BigDecimal.ZERO : (adj.max == null ? BigDecimal.ZERO : adj.max);
            min = base.min.add(add.min).subtract(dMax);
        }
        if (base.max != null && add.max != null) {
            BigDecimal dMin = adj == null ? BigDecimal.ZERO : (adj.min == null ? BigDecimal.ZERO : adj.min);
            max = base.max.add(add.max).subtract(dMin);
        }
        if (base.base != null && add.base != null) {
            BigDecimal dBase = adj == null ? BigDecimal.ZERO : (adj.base == null ? BigDecimal.ZERO : adj.base);
            baseRate = base.base.add(add.base).subtract(dBase);
        }
        if (min == null && max == null && baseRate == null) {
            return null;
        }
        return new RateStats(min, max, baseRate, "대출금리");
    }

    private BigDecimal pickDecimal(com.fasterxml.jackson.databind.JsonNode node, String... keys) {
        for (String key : keys) {
            com.fasterxml.jackson.databind.JsonNode v = node.get(key);
            if (v != null && !v.isNull()) {
                BigDecimal d = parseDecimal(v.asText());
                if (d != null) {
                    return d;
                }
            }
        }
        return null;
    }

    private String pickText(com.fasterxml.jackson.databind.JsonNode node, String... keys) {
        for (String key : keys) {
            com.fasterxml.jackson.databind.JsonNode v = node.get(key);
            if (v != null && !v.isNull()) {
                String s = v.asText();
                if (s != null && !s.isBlank()) {
                    return s;
                }
            }
        }
        return null;
    }

    private BigDecimal parseDecimal(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String safeBody(String body) {
        if (body == null) {
            return "";
        }
        if (body.length() <= 300) {
            return body;
        }
        return body.substring(0, 300) + "...";
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class FssCreditResponse {
        public FssCreditResult result;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class FssCreditResult {
        public String err_cd;
        public String err_msg;
        public List<FssCreditBase> baseList;
        public List<FssCreditOption> optionList;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class FssCreditBase {
        public String fin_prdt_cd;
        public String fin_prdt_nm;
        public String kor_co_nm;
        public String join_way;
        public String loan_inci_expn;
        public String dcls_end_day;
        public String fin_co_no;
        public String dcls_month;
        public String crdt_prdt_type;
        public String cb_name;
        public String crdt_prdt_type_nm;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class FssCreditOption {
        public String fin_prdt_cd;
        public String fin_co_no;
        public String crdt_lend_rate_type;
        public String crdt_lend_rate_type_nm;
        public String crdt_grad_1;
        public String crdt_grad_4;
        public String crdt_grad_5;
        public String crdt_grad_6;
        public String crdt_grad_10;
        public String crdt_grad_11;
        public String crdt_grad_12;
        public String crdt_grad_13;

        public List<String> gradeRates() {
            List<String> list = new ArrayList<>();
            list.add(crdt_grad_1);
            list.add(crdt_grad_4);
            list.add(crdt_grad_5);
            list.add(crdt_grad_6);
            list.add(crdt_grad_10);
            list.add(crdt_grad_11);
            list.add(crdt_grad_12);
            list.add(crdt_grad_13);
            return list;
        }
    }

    private static class RateStats {
        BigDecimal min;
        BigDecimal max;
        BigDecimal base;
        String rateTypeName;

        RateStats(BigDecimal min, BigDecimal max, BigDecimal base, String rateTypeName) {
            this.min = min;
            this.max = max;
            this.base = base;
            this.rateTypeName = rateTypeName;
        }
    }
}
