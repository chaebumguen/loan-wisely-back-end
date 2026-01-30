package com.ccksy.loan.domain.product.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LoanProduct {

    private Long productId;                  // PRODUCT.product_id (PK)
    private Long providerId;                 // PRODUCT.provider_id (FK -> PROVIDER)
    private String productName;              // PRODUCT.product_name

    private String productTypeCodeValueId;   // PRODUCT.product_type_code_value_id (FK -> CODE_VALUE)
    private String loanTypeCodeValueId;      // PRODUCT.loan_type_code_value_id
    private String repaymentTypeCodeValueId; // PRODUCT.repayment_type_code_value_id
    private String collateralTypeCodeValueId;// PRODUCT.collateral_type_code_value_id
    private String rateTypeCodeValueId;      // PRODUCT.rate_type_code_value_id

    private String note;                     // PRODUCT.note
    private LocalDateTime addDate;           // PRODUCT.add_date
    private LocalDate endDate;               // PRODUCT.end_date
    private LocalDateTime updatedAt;         // PRODUCT.updated_at (ERD에 표시)

    // join fields (PROVIDER)
    private String providerName;

    // latest rate snapshot (PRODUCT_INTEREST_RATE)
    private Long rateId;
    private Double rateBase;
    private Double rateMin;
    private Double rateMax;
    private LocalDate asOfDate;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductTypeCodeValueId() { return productTypeCodeValueId; }
    public void setProductTypeCodeValueId(String productTypeCodeValueId) { this.productTypeCodeValueId = productTypeCodeValueId; }

    public String getLoanTypeCodeValueId() { return loanTypeCodeValueId; }
    public void setLoanTypeCodeValueId(String loanTypeCodeValueId) { this.loanTypeCodeValueId = loanTypeCodeValueId; }

    public String getRepaymentTypeCodeValueId() { return repaymentTypeCodeValueId; }
    public void setRepaymentTypeCodeValueId(String repaymentTypeCodeValueId) { this.repaymentTypeCodeValueId = repaymentTypeCodeValueId; }

    public String getCollateralTypeCodeValueId() { return collateralTypeCodeValueId; }
    public void setCollateralTypeCodeValueId(String collateralTypeCodeValueId) { this.collateralTypeCodeValueId = collateralTypeCodeValueId; }

    public String getRateTypeCodeValueId() { return rateTypeCodeValueId; }
    public void setRateTypeCodeValueId(String rateTypeCodeValueId) { this.rateTypeCodeValueId = rateTypeCodeValueId; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getAddDate() { return addDate; }
    public void setAddDate(LocalDateTime addDate) { this.addDate = addDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public Long getRateId() { return rateId; }
    public void setRateId(Long rateId) { this.rateId = rateId; }

    public Double getRateBase() { return rateBase; }
    public void setRateBase(Double rateBase) { this.rateBase = rateBase; }

    public Double getRateMin() { return rateMin; }
    public void setRateMin(Double rateMin) { this.rateMin = rateMin; }

    public Double getRateMax() { return rateMax; }
    public void setRateMax(Double rateMax) { this.rateMax = rateMax; }

    public LocalDate getAsOfDate() { return asOfDate; }
    public void setAsOfDate(LocalDate asOfDate) { this.asOfDate = asOfDate; }
}
