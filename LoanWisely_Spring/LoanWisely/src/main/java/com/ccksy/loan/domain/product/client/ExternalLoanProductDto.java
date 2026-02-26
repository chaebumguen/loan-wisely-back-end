package com.ccksy.loan.domain.product.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 외부 상품 API 응답 DTO (최소 필드)
 */
@Getter
@Setter
@NoArgsConstructor
public class ExternalLoanProductDto {
    private Long productId;
    private Long providerId;
    private String productName;
    private String productTypeCodeValueId;
    private String loanTypeCodeValueId;
    private String repaymentTypeCodeValueId;
    private String collateralTypeCodeValueId;
    private String rateTypeCodeValueId;
    private String note;
    private LocalDate endDate;
    private String finPrdtCd;
    private String finCoNo;
    private String companyName;
    private String joinWay;
    private String loanInciExpn;
    private String cbName;
    private String productDetailType;
    private String productDetailTypeName;
    private String rateTypeName;
    private java.math.BigDecimal rateMin;
    private java.math.BigDecimal rateMax;
    private java.math.BigDecimal rateBase;
    private LocalDate asOfDate;
}
