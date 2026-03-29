package com.ccksy.loan.domain.recommend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 추천 요청 엔티티
 * - 설계서 기준 RECO_REQUEST 테이블 대응
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RecoRequest {

    private Long requestId;
    private Long userId;
    private Long versionId;
    private LocalDateTime requestedAt;
}
