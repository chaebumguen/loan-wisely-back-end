package com.ccksy.loan.domain.recommend.filter.chain;

import com.ccksy.loan.domain.recommend.filter.model.ExclusionReason;
import com.ccksy.loan.domain.recommend.filter.model.FilterContext;

import java.util.Optional;

public abstract class IneligibilityFilter {

    private IneligibilityFilter next;

    public IneligibilityFilter linkWith(IneligibilityFilter next) {
        this.next = next;
        return next;
    }

    public Optional<ExclusionReason> check(FilterContext ctx) {
        Optional<ExclusionReason> reason = doCheck(ctx);
        if (reason.isPresent()) return reason;
        if (next == null) return Optional.empty();
        return next.check(ctx);
    }

    protected abstract Optional<ExclusionReason> doCheck(FilterContext ctx);
}
