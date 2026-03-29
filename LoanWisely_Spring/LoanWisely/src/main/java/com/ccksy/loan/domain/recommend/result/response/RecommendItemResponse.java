package com.ccksy.loan.domain.recommend.result.response;

import com.ccksy.loan.domain.recommend.result.core.RecommendItem;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class RecommendItemResponse {

    private Long productId;
    private BigDecimal score;
    private BigDecimal minRate;
    private String briefReason;

    public static RecommendItemResponse from(RecommendItem item) {
        return RecommendItemResponse.builder()
                .productId(item.getProductId())
                .score(item.getScore())
                .minRate(item.getMinRate())
                .briefReason(item.getBriefReason())
                .build();
    }
}
