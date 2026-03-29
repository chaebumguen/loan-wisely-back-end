package com.ccksy.loan.domain.recommend.policy.scoring;

import com.ccksy.loan.domain.recommend.filter.model.FilterContext;

import java.math.BigDecimal;

public class RiskScoreStrategy implements ScoreStrategy {

    private final BigDecimal multiplier; // 리스크 점수(또는 creditScore 등) 반영 비율

    public RiskScoreStrategy(BigDecimal multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public BigDecimal score(FilterContext ctx, Long productId) {
        Integer creditScore = ctx.getCreditScore();
        if (creditScore == null) return BigDecimal.ZERO;

        // v1 단순 예시: (creditScore / 1000) * multiplier
        BigDecimal normalized = new BigDecimal(creditScore).divide(new BigDecimal("1000"), 6, java.math.RoundingMode.HALF_UP);
        return normalized.multiply(multiplier);
    }
}
