package com.ccksy.loan.domain.recommend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 추천 거절 로그 엔티티
 * - 설계서 기준 RECO_REJECT_LOG 테이블 대응
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RecoRejectLog {

    private Long rejectId;
    private Long requestId;
    private Long productId;

    private String rejectCode;
    private String rejectReason;

    private LocalDateTime createdAt;
}
