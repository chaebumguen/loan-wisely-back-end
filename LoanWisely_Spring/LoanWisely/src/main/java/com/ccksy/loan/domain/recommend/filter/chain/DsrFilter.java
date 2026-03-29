package com.ccksy.loan.domain.recommend.filter.chain;

import com.ccksy.loan.domain.recommend.filter.model.ExclusionReason;
import com.ccksy.loan.domain.recommend.filter.model.FilterContext;

import java.math.BigDecimal;
import java.util.Optional;

public class DsrFilter extends IneligibilityFilter {

    private final BigDecimal maxDsr; // 0.40 = 40%

    public DsrFilter(BigDecimal maxDsr) {
        this.maxDsr = maxDsr;
    }

    @Override
    protected Optional<ExclusionReason> doCheck(FilterContext ctx) {
        BigDecimal dsr = ctx.getDsr();
        if (dsr == null) {
            // LV3 미제공 시 DSR이 없을 수 있으므로 제외하지 않는다.
            return Optional.empty();
        }
        if (dsr.compareTo(maxDsr) > 0) {
            return Optional.of(ExclusionReason.of("DSR_TOO_HIGH",
                    "DSR가 기준을 초과했습니다.",
                    "max=" + maxDsr + ", actual=" + dsr));
        }
        return Optional.empty();
    }
}
