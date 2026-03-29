package com.ccksy.loan.domain.admin.rawfile.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RawFileEdaResponse {
    private Long edaRunId;
    private int metricCount;
    private int issueCount;
}
