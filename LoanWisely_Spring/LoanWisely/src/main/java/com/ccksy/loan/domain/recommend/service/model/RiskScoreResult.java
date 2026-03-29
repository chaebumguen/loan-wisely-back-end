package com.ccksy.loan.domain.recommend.service.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class RiskScoreResult {
    private final Integer creditScore;
    private final BigDecimal dsr;
    private final String loanPurposeCode;
}
