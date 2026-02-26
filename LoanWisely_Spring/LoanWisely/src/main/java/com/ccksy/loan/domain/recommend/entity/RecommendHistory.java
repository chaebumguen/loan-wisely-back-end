package com.ccksy.loan.domain.recommend.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RecommendHistory {

    private Long recommendId; // PK (DB에서 시퀀스로 생성)
    private Long userId;

    // reproduce key
    private String reproduceKey;

    // applied versions
    private String policyVersion;
    private String metaVersion;

    // evidence/explain
    private String evidenceFilePath;
    private String explainFilePath;
    private String explainSummary;

    private Long recoRequestId;
    private Long recoResultId;

    // state
    private String recommendState;

    // created time
    private LocalDateTime createdAt;
}
