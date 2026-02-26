package com.ccksy.loan.domain.recommend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 추천 결과 엔티티
 * - 설계서 기준 RECO_RESULT 테이블 대응
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RecoResult {

    private Long resultId;
    private Long requestId;

    private BigDecimal overallScore;
    private String policyVersion;
    private String confidenceLevelCodeValueId;
    private String explanationJsonPath;

    private LocalDateTime createdAt;
}
