// FILE: domain/recommend/dto/request/RecommendRequest.java
package com.ccksy.loan.domain.recommend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Collections;
import java.util.Map;

/**
 * 추천 실행 요청 DTO.
 *
 * v1 규칙:
 * - Controller는 이 DTO를 검증(@Valid)만 하고 판단/해석 로직은 금지
 * - 실제 userId는 인증 컨텍스트에서 획득(요청 바디에 포함하지 않음)
 * - 버전/옵션은 결정론(재현성) 근거로 사용됨
 */
public final class RecommendRequest {

    @NotBlank
    private String policyVersion;

    @NotBlank
    private String creditMetaVersion;

    @NotBlank
    private String financialMetaVersion;

    /**
     * 결정론 옵션(TopN, flags, sourceBatchId 등)
     * - v1에서는 스키마를 강제하지 않고 key/value로 받는다.
     */
    @NotNull
    private Map<String, Object> options = Collections.emptyMap();

    public RecommendRequest() {
        // Jackson
    }

    public String getPolicyVersion() {
        return policyVersion;
    }

    public void setPolicyVersion(String policyVersion) {
        this.policyVersion = policyVersion;
    }

    public String getCreditMetaVersion() {
        return creditMetaVersion;
    }

    public void setCreditMetaVersion(String creditMetaVersion) {
        this.creditMetaVersion = creditMetaVersion;
    }

    public String getFinancialMetaVersion() {
        return financialMetaVersion;
    }

    public void setFinancialMetaVersion(String financialMetaVersion) {
        this.financialMetaVersion = financialMetaVersion;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = (options == null) ? Collections.emptyMap() : options;
    }
}
