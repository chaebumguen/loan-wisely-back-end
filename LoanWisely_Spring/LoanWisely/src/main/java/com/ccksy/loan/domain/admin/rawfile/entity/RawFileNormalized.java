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
public class RawFileNormalized {
    private Long normId;
    private Long uploadId;
    private Long rowNum;
    private String columnName;
    private String columnValue;
    private LocalDateTime createdAt;
}
