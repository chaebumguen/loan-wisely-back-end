package com.ccksy.loan.domain.recommend.result.builder;

import com.ccksy.loan.domain.recommend.filter.model.ExclusionReason;
import com.ccksy.loan.domain.recommend.result.core.RecommendItem;

import java.math.BigDecimal;
import java.util.List;

public class RecommendItemBuilder {

    private RecommendItemBuilder() {}

    public static RecommendItem of(Long productId,
                                   BigDecimal score,
                                   BigDecimal minRate,
                                   String briefReason,
                                   List<ExclusionReason> exclusionReasons) {
        return RecommendItem.builder()
                .productId(productId)
                .score(score)
                .minRate(minRate)
                .briefReason(briefReason)
                .exclusionReasons(exclusionReasons)
                .build();
    }
}
