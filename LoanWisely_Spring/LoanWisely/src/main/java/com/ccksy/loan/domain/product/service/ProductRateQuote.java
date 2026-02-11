package com.ccksy.loan.domain.product.service;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class ProductRateQuote {

    private Long productId;
    private BigDecimal rateMin;
    private BigDecimal rateMax;
    private BigDecimal scoreBase;
    private String rateType;
    private LocalDate asOfDate;
}
