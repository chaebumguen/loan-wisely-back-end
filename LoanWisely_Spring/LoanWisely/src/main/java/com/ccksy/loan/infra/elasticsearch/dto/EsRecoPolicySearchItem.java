package com.ccksy.loan.infra.elasticsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EsRecoPolicySearchItem {
    private Long policyId;
    private String version;
    private String status;
    private String isActive;
    private String policyKey;
    private String approvedAt;
    private String createdAt;
}
