package com.ccksy.loan.domain.admin.audit.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    private Long auditId;
    private String actorId;
    private String actorRoles;
    private String action;
    private String target;
    private String detailJson;
    private LocalDateTime createdAt;
}
