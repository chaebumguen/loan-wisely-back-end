package com.ccksy.loan.domain.recommend.policy.scoring;

import com.ccksy.loan.domain.recommend.filter.model.FilterContext;

import java.math.BigDecimal;
import java.util.List;

public class CompositeScoreStrategy implements ScoreStrategy {

    private final List<ScoreStrategy> strategies;

    public CompositeScoreStrategy(List<ScoreStrategy> strategies) {
        this.strategies = strategies;
    }

    @Override
    public BigDecimal score(FilterContext ctx, Long productId) {
        BigDecimal total = BigDecimal.ZERO;
        for (ScoreStrategy s : strategies) {
            total = total.add(s.score(ctx, productId));
        }
        return total;
    }
}
