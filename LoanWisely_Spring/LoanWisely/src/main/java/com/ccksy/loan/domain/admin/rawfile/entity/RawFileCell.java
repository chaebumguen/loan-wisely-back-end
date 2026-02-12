package com.ccksy.loan.domain.admin.rawfile.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawFileCell {
    private Long cellId;
    private Long uploadId;
    private Long rowNum;
    private String columnName;
    private String columnValue;
    private LocalDateTime createdAt;
}
