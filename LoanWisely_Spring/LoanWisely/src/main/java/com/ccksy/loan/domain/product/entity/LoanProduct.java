package com.ccksy.loan.domain.product.entity;

import com.ccksy.loan.domain.product.dto.request.LoanProductRequest;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 외부 금융상품 기본 정보 엔티티
 * - 금리/한도 등 상세 조건은 별도 테이블에서 관리
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class LoanProduct {

    private Long productId; // PK

    private Long providerId; // FK

    private String productName;

    private String productTypeCodeValueId;
    private String loanTypeCodeValueId;
    private String repaymentTypeCodeValueId;

    private String collateralTypeCodeValueId;
    private String rateTypeCodeValueId;

    private String note;

    private String finPrdtCd;
    private String finCoNo;
    private String companyName;
    private String joinWay;
    private String cbName;

    private LocalDateTime addDate; // 등록일
    private LocalDate endDate;     // 종료일(옵션)
    private LocalDateTime updatedAt;

    public static LoanProduct from(LoanProductRequest r) {
        return LoanProduct.builder()
                .productId(r.getProductId())
                .providerId(r.getProviderId())
                .productName(r.getProductName())
                .productTypeCodeValueId(r.getProductTypeCodeValueId())
                .loanTypeCodeValueId(r.getLoanTypeCodeValueId())
                .repaymentTypeCodeValueId(r.getRepaymentTypeCodeValueId())
                .collateralTypeCodeValueId(r.getCollateralTypeCodeValueId())
                .rateTypeCodeValueId(r.getRateTypeCodeValueId())
                .note(r.getNote())
                .endDate(r.getEndDate())
                .build();
    }
}

