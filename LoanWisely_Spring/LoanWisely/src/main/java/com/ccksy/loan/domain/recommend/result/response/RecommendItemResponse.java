package com.ccksy.loan.domain.recommend.result.response;

/**
 * 추천 상품 응답 DTO
 *
 * <p>외부 API에 노출되는 단일 추천 상품 표현</p>
 */
public class RecommendItemResponse {

    private Long productId;
    private String productName;
    private double interestRate;
    private double score;

    // --- getter / setter ---

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
