package com.ccksy.loan.domain.admin.rawfile.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class EdaStatResult {
    private Long statId;
    private Long edaRunId;
    private Long rowId;
    private String columnCode;
    private BigDecimal mean;
    private BigDecimal median;
    private BigDecimal std;
    private BigDecimal min;
    private BigDecimal max;
    private BigDecimal q1;
    private BigDecimal q3;
    private BigDecimal skewness;
    private BigDecimal kurtosis;
    private BigDecimal missingRate;
    private String dataType;
}
