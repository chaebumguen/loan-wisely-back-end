package com.ccksy.loan.domain.recommend.policy.eligibility;

import com.ccksy.loan.domain.recommend.filter.model.FilterContext;

public interface EligibilityPolicy {
    boolean isEligible(FilterContext ctx);
}
