package com.ccksy.loan.domain.admin.rawfile.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RawFileNormalizeResponse {
    private int normalizedCount;
    private String status;
}
