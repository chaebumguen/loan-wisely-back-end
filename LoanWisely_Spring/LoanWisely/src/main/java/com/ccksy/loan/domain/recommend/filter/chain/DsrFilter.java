// FILE: domain/recommend/filter/chain/DsrFilter.java
package com.ccksy.loan.domain.recommend.filter.chain;

import com.ccksy.loan.domain.recommend.dto.internal.RecommendContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

@Component
public final class DsrFilter {

    public String id() {
        return "DsrFilter:v1";
    }

    /**
     * 입력 규약(v1):
     * - ctx.options["dsr"] : BigDecimal/Number/String (0~1 또는 0~100)
     * - candidate["maxDsr"] : BigDecimal/Number/String (없으면 통과)
     */
    public String apply(RecommendContext ctx, Map<String, Object> candidate) {
        Objects.requireNonNull(ctx, "ctx");
        Objects.requireNonNull(candidate, "candidate");

        BigDecimal userDsr = asDecimal(ctx.getOptions().get("dsr"));
        BigDecimal maxDsr = asDecimal(candidate.get("maxDsr"));

        if (maxDsr == null) return null;
        if (userDsr == null) return null; // v1 결측 통과

        userDsr = normalizeToRatio(userDsr);
        maxDsr = normalizeToRatio(maxDsr);

        if (userDsr.compareTo(maxDsr) > 0) {
            return "RECO_EXCLUDE_DSR_TOO_HIGH";
        }
        return null;
    }

    private static BigDecimal normalizeToRatio(BigDecimal v) {
        if (v.compareTo(BigDecimal.ONE) > 0) {
            return v.divide(BigDecimal.valueOf(100), 8, java.math.RoundingMode.HALF_UP);
        }
        return v;
    }

    private static BigDecimal asDecimal(Object v) {
        if (v == null) return null;
        if (v instanceof BigDecimal) return (BigDecimal) v;
        if (v instanceof Number) return BigDecimal.valueOf(((Number) v).doubleValue());
        try {
            String s = String.valueOf(v).trim();
            if (s.isEmpty()) return null;
            return new BigDecimal(s);
        } catch (Exception ignored) {
            return null;
        }
    }
}
