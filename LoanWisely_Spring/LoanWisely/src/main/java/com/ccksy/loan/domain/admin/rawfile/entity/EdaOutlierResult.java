package com.ccksy.loan.domain.admin.rawfile.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class EdaOutlierResult {
    private Long outlierId;
    private Long edaRunId;
    private Long rowId;
    private String columnCode;
    private String methodCodeValueId;
    private String flag;
    private String reason;
}
