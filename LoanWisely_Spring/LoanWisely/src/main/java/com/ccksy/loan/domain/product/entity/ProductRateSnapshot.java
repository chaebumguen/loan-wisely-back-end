package com.ccksy.loan.domain.product.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRateSnapshot {

    private Long rateId;
    private Long productId;
    private BigDecimal rateMin;
    private BigDecimal rateMax;
    private BigDecimal scoreBase;
    private String rateType;
    private LocalDate asOfDate;
    private LocalDateTime createdAt;
}
