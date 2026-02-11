package com.ccksy.loan.domain.recommend.filter.chain;

import com.ccksy.loan.domain.recommend.filter.model.ExclusionReason;
import com.ccksy.loan.domain.recommend.filter.model.FilterContext;

import java.util.Optional;

public class CreditScoreFilter extends IneligibilityFilter {

    private final int minCreditScore;

    public CreditScoreFilter(int minCreditScore) {
        this.minCreditScore = minCreditScore;
    }

    @Override
    protected Optional<ExclusionReason> doCheck(FilterContext ctx) {
        Integer score = ctx.getCreditScore();
        if (score == null) {
            return Optional.of(ExclusionReason.of("CREDIT_SCORE_MISSING", "신용점수 정보가 없어 추천이 불가합니다."));
        }
        if (score < minCreditScore) {
            return Optional.of(ExclusionReason.of("CREDIT_SCORE_TOO_LOW",
                    "신용점수가 기준 미달입니다.",
                    "min=" + minCreditScore + ", actual=" + score));
        }
        return Optional.empty();
    }
}
