package com.ccksy.loan.infra.elasticsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EsRecommendHistorySearchItem {
    private Long recommendId;
    private Long userId;
    private String policyVersion;
    private String recommendState;
    private String explainSummary;
    private String createdAt;
    private Long recoRequestId;
    private Long recoResultId;
}
