package com.ccksy.loan.domain.recommend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 추천 제외 사유 엔티티
 * - 설계서 기준 RECO_EXCLUSION_REASON 테이블 대응
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RecoExclusionReason {

    private Long reasonId;
    private Long resultId;
    private Long productId;

    private String reasonCode;
    private String reasonText;

    private LocalDateTime createdAt;
}
