package com.ccksy.loan.domain.recommend.policy.scoring;

import com.ccksy.loan.domain.recommend.filter.model.FilterContext;

import java.math.BigDecimal;
import java.util.Map;

public class PurposeScoreStrategy implements ScoreStrategy {

    private final Map<String, BigDecimal> purposeWeight; // 목적 코드별 가중치

    public PurposeScoreStrategy(Map<String, BigDecimal> purposeWeight) {
        this.purposeWeight = purposeWeight;
    }

    @Override
    public BigDecimal score(FilterContext ctx, Long productId) {
        String purpose = ctx.getLoanPurposeCode();
        String normalized = normalizePurpose(purpose);
        if (normalized == null) {
            return BigDecimal.ZERO;
        }
        return purposeWeight.getOrDefault(normalized, BigDecimal.ZERO);
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
            return "CREDIT";
        }
        if (v.contains("BUSINESS") || v.contains("사업")) {
            return "CREDIT";
        }
        if (v.contains("HOUSING") || v.contains("주택") || v.contains("모기지")) {
            return "MORTGAGE";
        }
        if (v.contains("RENT") || v.contains("JEONSE") || v.contains("전세") || v.contains("임대")) {
            return "RENT";
        }
        if (v.equals("CREDIT") || v.equals("MORTGAGE") || v.equals("RENT")) {
            return v;
        }
        return null;
    }
}

