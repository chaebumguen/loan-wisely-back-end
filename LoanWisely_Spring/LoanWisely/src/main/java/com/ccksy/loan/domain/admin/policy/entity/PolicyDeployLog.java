package com.ccksy.loan.domain.admin.policy.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDeployLog {
    private Long deployId;
    private Long policyId;
    private Long previousPolicyId;
    private String action;
    private String reason;
    private String actorId;
    private LocalDateTime deployedAt;
}
