package com.ccksy.loan.domain.recommend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 추천 이벤트 로그 엔티티
 * - 설계서 기준 RECO_EVENT_LOG 테이블 대응
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RecoEventLog {

    private Long logId;
    private String maskedUserId;
    private Long productId;

    private String eventTypeCodeValueId;
    private LocalDateTime occurredAt;
}
