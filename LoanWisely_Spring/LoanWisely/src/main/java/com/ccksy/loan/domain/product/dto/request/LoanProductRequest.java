package com.ccksy.loan.domain.product.dto.request;

public class LoanProductRequest {

    // filters
    private Long providerId;
    private String productNameKeyword;

    private String productTypeCodeValueId;
    private String loanTypeCodeValueId;
    private String repaymentTypeCodeValueId;
    private String collateralTypeCodeValueId;
    private String rateTypeCodeValueId;

    // rate filters (latest snapshot 기준)
    private Double maxRateUpperBound; // rate_max <= ?
    private Double minRateUpperBound; // rate_min <= ?

    // pagination
    private Integer page = 1;   // 1-based
    private Integer size = 20;

    // sorting (화이트리스트)
    private String sortBy = "rateMin"; // rateMin | rateMax | productName | asOfDate
    private String sortDir = "ASC";    // ASC | DESC

    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }

    public String getProductNameKeyword() { return productNameKeyword; }
    public void setProductNameKeyword(String productNameKeyword) { this.productNameKeyword = productNameKeyword; }

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

    public Double getMaxRateUpperBound() { return maxRateUpperBound; }
    public void setMaxRateUpperBound(Double maxRateUpperBound) { this.maxRateUpperBound = maxRateUpperBound; }

    public Double getMinRateUpperBound() { return minRateUpperBound; }
    public void setMinRateUpperBound(Double minRateUpperBound) { this.minRateUpperBound = minRateUpperBound; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortDir() { return sortDir; }
    public void setSortDir(String sortDir) { this.sortDir = sortDir; }
}
