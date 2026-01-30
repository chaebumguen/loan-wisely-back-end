package com.ccksy.loan.domain.product.dto.response;

import java.time.LocalDate;

public class LoanProductResponse {

    private Long productId;
    private Long providerId;
    private String providerName;

    private String productName;

    private String productTypeCodeValueId;
    private String loanTypeCodeValueId;
    private String repaymentTypeCodeValueId;
    private String collateralTypeCodeValueId;
    private String rateTypeCodeValueId;

    private Double rateBase;
    private Double rateMin;
    private Double rateMax;
    private LocalDate asOfDate;

    private String note;
    private LocalDate endDate;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

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

    public Double getRateBase() { return rateBase; }
    public void setRateBase(Double rateBase) { this.rateBase = rateBase; }

    public Double getRateMin() { return rateMin; }
    public void setRateMin(Double rateMin) { this.rateMin = rateMin; }

    public Double getRateMax() { return rateMax; }
    public void setRateMax(Double rateMax) { this.rateMax = rateMax; }

    public LocalDate getAsOfDate() { return asOfDate; }
    public void setAsOfDate(LocalDate asOfDate) { this.asOfDate = asOfDate; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}
