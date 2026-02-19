package com.ccksy.loan.domain.recommend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 추천 정책 엔티티
 * - 추천 정책 테이블(RECO_POLICY) 매핑
 */
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RecoPolicy {

    private Long policyId;
    private String version;
    private String policyTypeCodeValueId;
    private String policyKey;
    private String policyValue;
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
    private String status;
    private LocalDateTime approvedAt;
    private String approvedBy;
    private String isActive;
    private LocalDateTime createdAt;
}

