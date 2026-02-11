package com.ccksy.loan.domain.product.entity;

import com.ccksy.loan.domain.product.dto.request.LoanProductRequest;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 대출 상품 엔티티(기초정보)
 * - 금리/상세 조건 등은 별도 테이블/스냅샷에서 확장될 수 있음(추천/재현 요구 대응)
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

    private LocalDateTime addDate; // 등록 시각
    private LocalDate endDate;     // 판매 종료일(옵션)
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
