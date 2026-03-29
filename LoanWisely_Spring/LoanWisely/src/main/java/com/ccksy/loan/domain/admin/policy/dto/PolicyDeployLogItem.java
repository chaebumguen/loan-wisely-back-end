package com.ccksy.loan.domain.admin.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PolicyDeployLogItem {
    private Long policyId;
    private Long previousPolicyId;
    private String action;
    private String reason;
    private String actorId;
    private String deployedAt;
}
