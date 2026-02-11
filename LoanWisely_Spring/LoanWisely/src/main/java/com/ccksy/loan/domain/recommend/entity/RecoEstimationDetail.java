package com.ccksy.loan.domain.recommend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 추천 추정 상세 엔티티
 * - 설계서 기준 RECO_ESTIMATION_DETAIL 테이블 대응
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RecoEstimationDetail {

    private Long detailId;
    private Long snapshotId;
    private Long itemId;
    private String factorCode;
    private String factorName;
    private String factorValue;
    private BigDecimal contribution;
    private LocalDateTime createdAt;
}
