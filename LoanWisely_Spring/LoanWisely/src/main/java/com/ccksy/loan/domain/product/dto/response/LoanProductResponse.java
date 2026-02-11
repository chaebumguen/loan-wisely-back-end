package com.ccksy.loan.domain.product.dto.response;

import com.ccksy.loan.domain.product.entity.LoanProduct;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class LoanProductResponse {

    private Long productId;
    private Long providerId;

    private String productName;

    private String productTypeCodeValueId;
    private String loanTypeCodeValueId;
    private String repaymentTypeCodeValueId;

    private String collateralTypeCodeValueId;
    private String rateTypeCodeValueId;

    private String note;

    private LocalDateTime addDate;
    private LocalDate endDate;
    private LocalDateTime updatedAt;

    public static LoanProductResponse from(LoanProduct e) {
        return LoanProductResponse.builder()
                .productId(e.getProductId())
                .providerId(e.getProviderId())
                .productName(e.getProductName())
                .productTypeCodeValueId(e.getProductTypeCodeValueId())
                .loanTypeCodeValueId(e.getLoanTypeCodeValueId())
                .repaymentTypeCodeValueId(e.getRepaymentTypeCodeValueId())
                .collateralTypeCodeValueId(e.getCollateralTypeCodeValueId())
                .rateTypeCodeValueId(e.getRateTypeCodeValueId())
                .note(e.getNote())
                .addDate(e.getAddDate())
                .endDate(e.getEndDate())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
