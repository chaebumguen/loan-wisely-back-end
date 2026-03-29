package com.ccksy.loan.domain.recommend.policy.eligibility;

import com.ccksy.loan.domain.recommend.filter.model.FilterContext;

public class DefaultEligibilityPolicy implements EligibilityPolicy {

    @Override
    public boolean isEligible(FilterContext ctx) {
        // v1 최소 조건: LV 식별 + userId
        if (ctx.getUserId() == null) return false;
        if (ctx.getInputLv() == null) return false;
        return ctx.getInputLv() >= 1 && ctx.getInputLv() <= 3;
    }
}
