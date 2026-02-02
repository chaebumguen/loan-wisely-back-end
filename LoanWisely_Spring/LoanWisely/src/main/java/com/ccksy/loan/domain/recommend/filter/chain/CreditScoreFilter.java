// FILE: domain/recommend/filter/chain/CreditScoreFilter.java
package com.ccksy.loan.domain.recommend.filter.chain;

import com.ccksy.loan.domain.recommend.dto.internal.RecommendContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
public final class CreditScoreFilter {

    public String id() {
        return "CreditScoreFilter:v1";
    }

    /**
     * 입력 규약(v1):
     * - ctx.options["creditScore"] : Number/String
     * - candidate["minCreditScore"] : Number/String (없으면 통과)
     */
    public String apply(RecommendContext ctx, Map<String, Object> candidate) {
        Objects.requireNonNull(ctx, "ctx");
        Objects.requireNonNull(candidate, "candidate");

        Long userScore = asLong(ctx.getOptions().get("creditScore"));
        Long minRequired = asLong(candidate.get("minCreditScore"));

        if (minRequired == null) return null;
        if (userScore == null) return null; // v1: 결측은 통과(결측전략은 v2 Strategy로)

        if (userScore < minRequired) {
            return "RECO_EXCLUDE_CREDIT_SCORE_LOW";
        }
        return null;
    }

    private static Long asLong(Object v) {
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).longValue();
        try {
            String s = String.valueOf(v).trim();
            if (s.isEmpty()) return null;
            return Long.parseLong(s);
        } catch (Exception ignored) {
            return null;
        }
    }
}
