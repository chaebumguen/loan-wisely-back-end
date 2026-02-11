package com.ccksy.loan.domain.recommend.policy.scoring;

import com.ccksy.loan.domain.recommend.filter.model.FilterContext;

import java.math.BigDecimal;
import java.util.Map;

public class PurposeScoreStrategy implements ScoreStrategy {

    private final Map<String, BigDecimal> purposeWeight; // 목적코드 → 가중치

    public PurposeScoreStrategy(Map<String, BigDecimal> purposeWeight) {
        this.purposeWeight = purposeWeight;
    }

    @Override
    public BigDecimal score(FilterContext ctx, Long productId) {
        String purpose = ctx.getLoanPurposeCode();
        if (purpose == null) return BigDecimal.ZERO;
        return purposeWeight.getOrDefault(purpose, BigDecimal.ZERO);
    }
}
