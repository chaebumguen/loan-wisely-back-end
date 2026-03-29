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
public class RawFileRow {
    private Long rowId;
    private Long uploadId;
    private Long rowNum;
    private String rowJson;
    private LocalDateTime createdAt;
}
