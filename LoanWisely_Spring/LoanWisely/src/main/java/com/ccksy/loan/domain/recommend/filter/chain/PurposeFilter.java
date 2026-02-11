package com.ccksy.loan.domain.recommend.filter.chain;

import com.ccksy.loan.domain.recommend.filter.model.ExclusionReason;
import com.ccksy.loan.domain.recommend.filter.model.FilterContext;

import java.util.Optional;
import java.util.Set;

public class PurposeFilter extends IneligibilityFilter {

    private final Set<String> allowedPurposeCodes;

    public PurposeFilter(Set<String> allowedPurposeCodes) {
        this.allowedPurposeCodes = allowedPurposeCodes;
    }

    @Override
    protected Optional<ExclusionReason> doCheck(FilterContext ctx) {
        String purpose = ctx.getLoanPurposeCode();
        if (purpose == null || purpose.isBlank()) {
            // “미제공 시 불이익 금지” 원칙이 있어(요구사항) v1에서는 미제공은 통과 처리
            return Optional.empty();
        }
        if (!allowedPurposeCodes.contains(purpose)) {
            return Optional.of(ExclusionReason.of("PURPOSE_NOT_ALLOWED",
                    "대출 목적이 정책상 허용되지 않습니다.",
                    "purpose=" + purpose));
        }
        return Optional.empty();
    }
}
