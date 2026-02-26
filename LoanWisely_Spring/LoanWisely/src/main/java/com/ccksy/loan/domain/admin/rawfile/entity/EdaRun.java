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
public class EdaRun {
    private Long edaRunId;
    private Long userId;
    private Long snapshotId;
    private Long versionId;
    private LocalDateTime createdAt;
}
