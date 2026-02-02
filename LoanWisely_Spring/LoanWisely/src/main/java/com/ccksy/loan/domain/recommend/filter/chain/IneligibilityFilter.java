// FILE: domain/recommend/filter/chain/IneligibilityFilter.java
package com.ccksy.loan.domain.recommend.filter.chain;

import com.ccksy.loan.domain.recommend.dto.internal.RecommendContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
public final class IneligibilityFilter {

    public String id() {
        return "IneligibilityFilter:v1";
    }

    /**
     * 입력 규약(v1):
     * - candidate["eligible"] : Boolean/String ("true"/"false") (없으면 true)
     * - candidate["ineligibleReasonCode"] : String (선택)
     */
    public String apply(RecommendContext ctx, Map<String, Object> candidate) {
        // ctx는 현재 로직에서 사용하지 않지만 시그니처 통일(향후 LV/동의 상태 반영 여지)
        Objects.requireNonNull(ctx, "ctx");
        Objects.requireNonNull(candidate, "candidate");

        Boolean eligible = asBoolean(candidate.get("eligible"));
        if (eligible == null) eligible = Boolean.TRUE;

        if (!eligible) {
            Object code = candidate.get("ineligibleReasonCode");
            if (code != null && !String.valueOf(code).trim().isEmpty()) {
                return "RECO_EXCLUDE_INELIGIBLE:" + String.valueOf(code).trim();
            }
            return "RECO_EXCLUDE_INELIGIBLE";
        }
        return null;
    }

    private static Boolean asBoolean(Object v) {
        if (v == null) return null;
        if (v instanceof Boolean) return (Boolean) v;
        String s = String.valueOf(v).trim().toLowerCase();
        if (s.isEmpty()) return null;
        if ("true".equals(s) || "y".equals(s) || "yes".equals(s) || "1".equals(s)) return Boolean.TRUE;
        if ("false".equals(s) || "n".equals(s) || "no".equals(s) || "0".equals(s)) return Boolean.FALSE;
        return null;
    }
}
