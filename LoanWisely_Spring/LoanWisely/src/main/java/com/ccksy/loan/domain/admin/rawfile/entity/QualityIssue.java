package com.ccksy.loan.domain.admin.rawfile.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class QualityIssue {
    private Long issueId;
    private Long uploadId;
    private String issueTypeCodeValueId;
    private String columnCode;
    private String detailJsonPath;
    private String statusCodeValueId;
    private Long resolvedByUserId;
    private LocalDateTime resolvedAt;
    private String detectedStageCodeValueId;
    private LocalDateTime issuedAt;
}
