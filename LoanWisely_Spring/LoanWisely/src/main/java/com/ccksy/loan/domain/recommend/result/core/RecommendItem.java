package com.ccksy.loan.domain.recommend.result.core;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class RecommendItem {

    private Long productId;

    private BigDecimal score;   // 적합도 점수(정렬 기준)
    private BigDecimal minRate; // 금리 기준 정렬 시 사용(없으면 null)

    private String briefReason; // 간단 사유(옵션)
}
