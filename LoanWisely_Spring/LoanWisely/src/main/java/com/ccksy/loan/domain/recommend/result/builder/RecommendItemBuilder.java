package com.ccksy.loan.domain.recommend.result.builder;

import java.util.Objects;

import com.ccksy.loan.domain.recommend.result.core.RecommendItem;

/**
 * 개별 추천 상품 Builder
 *
 * <p>RecommendItem 생성 책임을 단일화한다.</p>
 */
public class RecommendItemBuilder {

    private Long productId;
    private String productName;
    private double interestRate;
    private double score;

    public RecommendItemBuilder productId(Long productId) {
        this.productId = productId;
        return this;
    }

    public RecommendItemBuilder productName(String productName) {
        this.productName = productName;
        return this;
    }

    public RecommendItemBuilder interestRate(double interestRate) {
        this.interestRate = interestRate;
        return this;
    }

    public RecommendItemBuilder score(double score) {
        this.score = score;
        return this;
    }

    public RecommendItem build() {
        Objects.requireNonNull(productId, "productId must not be null.");
        Objects.requireNonNull(productName, "productName must not be null.");

        RecommendItem item = new RecommendItem();
        item.setProductId(productId);
        item.setProductName(productName);
        item.setInterestRate(interestRate);
        item.setScore(score);
        return item;
    }
}
