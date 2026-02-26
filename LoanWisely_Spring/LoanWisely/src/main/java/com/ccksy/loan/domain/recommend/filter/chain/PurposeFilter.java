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
            return Optional.empty();
        }
        String normalized = normalizePurpose(purpose);
        if (normalized == null || !allowedPurposeCodes.contains(normalized)) {
            return Optional.of(ExclusionReason.of("PURPOSE_NOT_ALLOWED",
                    "대출 목적이 정책상 허용되지 않습니다.",
                    "purpose=" + purpose));
        }
        return Optional.empty();
    }

    private String normalizePurpose(String purpose) {
        if (purpose == null) {
            return null;
        }
        String v = purpose.trim().toUpperCase();
        if (v.isEmpty()) {
            return null;
        }
        if (v.contains("LIVING") || v.contains("생활")) {
            return "LIVING";
        }
        if (v.contains("RENT") || v.contains("JEONSE") || v.contains("전세") || v.contains("임대")) {
            return "RENT";
        }
        if (v.contains("HOUSING") || v.contains("주택") || v.contains("모기지")) {
            return "HOUSING";
        }
        if (v.contains("BUSINESS") || v.contains("사업")) {
            return "BUSINESS";
        }
        if (v.contains("EDU") || v.contains("교육")) {
            return "EDU";
        }
        if (v.contains("ETC") || v.contains("기타")) {
            return "ETC";
        }
        if (v.equals("LIVING") || v.equals("HOUSING") || v.equals("RENT") || v.equals("BUSINESS") || v.equals("EDU") || v.equals("ETC")) {
            return v;
        }
        return v;
    }
}
