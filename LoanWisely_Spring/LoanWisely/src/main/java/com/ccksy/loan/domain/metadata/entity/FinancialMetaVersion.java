package com.ccksy.loan.domain.metadata.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FinancialMetaVersion {
    private Long versionId;
    private String versionLabel;
    private String metaJson;
    private String status;
    private LocalDateTime approvedAt;
    private String approvedBy;
    private String isActive;
    private LocalDateTime createdAt;
}
