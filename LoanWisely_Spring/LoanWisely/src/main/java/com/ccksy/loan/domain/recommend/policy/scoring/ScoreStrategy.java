package com.ccksy.loan.domain.recommend.policy.scoring;

import com.ccksy.loan.domain.recommend.filter.model.FilterContext;

import java.math.BigDecimal;

public interface ScoreStrategy {
    BigDecimal score(FilterContext ctx, Long productId);
}
