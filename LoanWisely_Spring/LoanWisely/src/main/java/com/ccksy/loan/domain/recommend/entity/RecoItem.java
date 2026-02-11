package com.ccksy.loan.domain.recommend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 추천 아이템 엔티티
 * - 설계서 기준 RECO_ITEM 테이블 대응
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RecoItem {

    private Long itemId;
    private Long resultId;
    private Long productId;

    private BigDecimal matchingScore;
    private BigDecimal estimatedRate;
    private BigDecimal estimatedLimit;
    private BigDecimal stabilityScore;

    private String reasonJsonPath;
    private Integer rank;

    private LocalDateTime createdAt;
}
