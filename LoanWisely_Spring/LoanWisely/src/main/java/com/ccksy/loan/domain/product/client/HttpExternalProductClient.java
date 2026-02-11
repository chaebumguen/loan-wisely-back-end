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
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
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

    @Value("${product.external.fss.page-no:1}")
    private String pageNo;

    @Value("${product.external.fss.finance-cd:}")
    private String financeCd;

    @Override
    public List<ExternalLoanProductDto> fetchProducts() {
        if (creditUrl == null || creditUrl.isBlank()) {
            return Collections.emptyList();
        }

        String url = buildFssCreditUrl();
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(TIMEOUT)
                    .GET()
                    .header("Accept", "application/json")
                    .header("User-Agent", "LoanWisely/1.0");

            HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                log.warn("FSS credit API non-2xx status: {} body={}", response.statusCode(), safeBody(response.body()));
                return Collections.emptyList();
            }

            String contentType = response.headers().firstValue("Content-Type").orElse("");
            if (!contentType.toLowerCase().contains("json")) {
                log.warn("FSS credit API non-JSON response. contentType={} body={}", contentType, safeBody(response.body()));
                return Collections.emptyList();
            }

            FssCreditResponse fssResponse = objectMapper.readValue(response.body(), FssCreditResponse.class);
            if (fssResponse == null || fssResponse.result == null || fssResponse.result.baseList == null) {
                return Collections.emptyList();
            }
            if (fssResponse.result.err_cd != null && !"000".equals(fssResponse.result.err_cd)) {
                log.warn("FSS credit API error. err_cd={} err_msg={}", fssResponse.result.err_cd, fssResponse.result.err_msg);
                return Collections.emptyList();
            }
            log.info("FSS credit API ok. baseListSize={}", fssResponse.result.baseList.size());

            return fssResponse.result.baseList.stream()
                    .map(this::toExternalDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("FSS credit API request failed. url={}", url, e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "External product API request failed.");
        }
    }

    private String buildFssCreditUrl() {
        StringBuilder sb = new StringBuilder(creditUrl);
        String queryPrefix = creditUrl.contains("?") ? "&" : "?";
        sb.append(queryPrefix);
        sb.append(encodeParam(apiKeyParam, apiKey));

        if (responseType != null && !responseType.isBlank()) {
            sb.append("&").append(encodeParam("responseType", responseType));
        }

        String finGrp = (topFinGrpNosCredit != null && !topFinGrpNosCredit.isBlank())
                ? topFinGrpNosCredit
                : topFinGrpNos;
        if (finGrp != null && !finGrp.isBlank()) {
            sb.append("&").append(encodeParam("topFinGrpNo", finGrp));
        }

        if (pageNo != null && !pageNo.isBlank()) {
            sb.append("&").append(encodeParam("pageNo", pageNo));
        }

        if (financeCd != null && !financeCd.isBlank()) {
            sb.append("&").append(encodeParam("financeCd", financeCd));
        }

        return sb.toString();
    }

    private String encodeParam(String key, String value) {
        return URLEncoder.encode(key, StandardCharsets.UTF_8) + "="
                + URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private ExternalLoanProductDto toExternalDto(FssCreditBase base) {
        ExternalLoanProductDto dto = new ExternalLoanProductDto();
        dto.setProductName(base.fin_prdt_nm);
        dto.setProductTypeCodeValueId("CREDIT");
        dto.setCompanyName(base.kor_co_nm);
        dto.setFinCoNo(base.fin_co_no);
        dto.setFinPrdtCd(base.fin_prdt_cd);
        dto.setJoinWay(base.join_way);
        dto.setLoanInciExpn(base.loan_inci_expn);
        dto.setNote(base.join_way != null && !base.join_way.isBlank() ? base.join_way : base.loan_inci_expn);
        dto.setEndDate(parseFssDate(base.dcls_end_day));
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
    }
}
