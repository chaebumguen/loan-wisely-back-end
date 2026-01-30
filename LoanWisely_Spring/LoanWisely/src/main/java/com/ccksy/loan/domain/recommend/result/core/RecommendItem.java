package com.ccksy.loan.domain.recommend.result.core;

import java.util.List;

/**
 * 추천 상품 핵심 모델
 *
 * <p>점수, 금리 등 추천 결과의 최소 단위를 표현한다.</p>
 */
public class RecommendItem {

    private Long productId;
    private String productName;
    private double interestRate;
    private double score;

    /**
     * 상품 목적 태그 (scoring/filter 에서 사용)
     */
    private List<String> purposeTags;

    /**
     * 상품 위험도 (scoring/filter 에서 사용)
     */
    private Integer riskLevel;

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

    public List<String> getPurposeTags() {
        return purposeTags;
    }

    public void setPurposeTags(List<String> purposeTags) {
        this.purposeTags = purposeTags;
    }

    public Integer getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(Integer riskLevel) {
        this.riskLevel = riskLevel;
    }
}
