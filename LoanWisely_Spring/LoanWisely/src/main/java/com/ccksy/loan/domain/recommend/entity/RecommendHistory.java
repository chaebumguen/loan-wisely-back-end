// FILE: domain/recommend/entity/RecommendHistory.java
package com.ccksy.loan.domain.recommend.entity;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 추천 이력/증거 루트 엔티티 (v1 최소 단위).
 *
 * 설계 의도:
 * - 추천 1회 실행의 "증거(Evidence)"를 식별하고 추적하는 루트
 * - 판단/정책 해석/점수 계산 로직 포함 금지 (process 책임)
 * - mapper 계층에서 RECO_REQUEST/RECO_RESULT 등과 매핑되어 저장됨
 */
public final class RecommendHistory {

    private final Long recommendationId;
    private final Long userId;

    /** 결정론/재현 키(동일 입력+동일 버전이면 동일) */
    private final String determinismKey;

    /** Approved-only 참조값(재현 근거) */
    private final String policyVersion;
    private final String creditMetaVersion;
    private final String financialMetaVersion;

    /** 상태: 저장/설명 Gate 기준으로 유효/무효/차단 */
    private final Status status;

    /** Explain 저장 위치(또는 경로). v1은 string path로만 보관 */
    private final String explanationJsonPath;

    /** 감사용 시간(결정에 영향 주면 안 됨) */
    private final LocalDateTime createdAt;

    public enum Status {
        REQUESTED,
        VALID,
        INVALID,
        BLOCKED
    }

    public RecommendHistory(
            Long recommendationId,
            Long userId,
            String determinismKey,
            String policyVersion,
            String creditMetaVersion,
            String financialMetaVersion,
            Status status,
            String explanationJsonPath,
            LocalDateTime createdAt
    ) {
        this.recommendationId = Objects.requireNonNull(recommendationId, "recommendationId");
        this.userId = Objects.requireNonNull(userId, "userId");
        this.determinismKey = requireText(determinismKey, "determinismKey");
        this.policyVersion = requireText(policyVersion, "policyVersion");
        this.creditMetaVersion = requireText(creditMetaVersion, "creditMetaVersion");
        this.financialMetaVersion = requireText(financialMetaVersion, "financialMetaVersion");
        this.status = Objects.requireNonNull(status, "status");
        this.explanationJsonPath = explanationJsonPath == null ? null : explanationJsonPath.trim();
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public Long getRecommendationId() {
        return recommendationId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getDeterminismKey() {
        return determinismKey;
    }

    public String getPolicyVersion() {
        return policyVersion;
    }

    public String getCreditMetaVersion() {
        return creditMetaVersion;
    }

    public String getFinancialMetaVersion() {
        return financialMetaVersion;
    }

    public Status getStatus() {
        return status;
    }

    public String getExplanationJsonPath() {
        return explanationJsonPath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.trim();
    }
}
