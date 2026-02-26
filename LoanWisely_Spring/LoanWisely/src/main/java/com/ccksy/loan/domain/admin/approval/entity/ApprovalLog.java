package com.ccksy.loan.domain.admin.approval.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalLog {
    private Long approvalId;
    private String targetId;
    private String action;
    private String reason;
    private String actorId;
    private LocalDateTime createdAt;
}
