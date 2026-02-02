// FILE: domain/recommend/filter/chain/PurposeFilter.java
package com.ccksy.loan.domain.recommend.filter.chain;

import com.ccksy.loan.domain.recommend.dto.internal.RecommendContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Component
public final class PurposeFilter {

    public String id() {
        return "PurposeFilter:v1";
    }

    /**
     * 입력 규약(v1):
     * - ctx.options["loanPurpose"] : String
     * - candidate["allowedPurposes"] : String (콤마 구분 또는 "*" 허용)
     */
    public String apply(RecommendContext ctx, Map<String, Object> candidate) {
        Objects.requireNonNull(ctx, "ctx");
        Objects.requireNonNull(candidate, "candidate");

        String purpose = asText(ctx.getOptions().get("loanPurpose"));
        String allowed = asText(candidate.get("allowedPurposes"));

        if (purpose == null) return null;
        if (allowed == null || "*".equals(allowed)) return null;

        Set<String> allowedSet = parseAllowed(allowed);
        if (!allowedSet.contains(purpose)) {
            return "RECO_EXCLUDE_PURPOSE_NOT_ALLOWED";
        }
        return null;
    }

    private static Set<String> parseAllowed(String allowed) {
        String[] parts = allowed.split(",");
        Set<String> set = new HashSet<>();
        Arrays.stream(parts)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .forEach(set::add);
        return set;
    }

    private static String asText(Object v) {
        if (v == null) return null;
        String s = String.valueOf(v).trim();
        return s.isEmpty() ? null : s;
    }
}
