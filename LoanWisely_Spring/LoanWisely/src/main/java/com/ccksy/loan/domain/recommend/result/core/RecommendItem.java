package com.ccksy.loan.domain.recommend.result.core;

import com.ccksy.loan.domain.recommend.filter.model.ExclusionReason;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class RecommendItem {

    private Long productId;

    // Matching score for sorting
    private BigDecimal score;

    // Minimum rate for sorting (nullable)
    private BigDecimal minRate;

    // Short reason text (optional)
    private String briefReason;

    // Product-specific exclusion reasons
    private List<ExclusionReason> exclusionReasons;
}
