// FILE: domain/recommend/command/RecommendCommandLog.java
package com.ccksy.loan.domain.recommend.command;

import java.time.LocalDateTime;

/**
 * (Command Log) Command 실행에 대한 감사/추적용 로그 객체.
 *
 * v1 규칙:
 * - command 패키지에서 "DB 저장"은 하지 않는다.
 * - 실제 저장은 process/mapper 계층에서 RecommendHistory/RecommendEventLog 등으로 흡수한다.
 */
public final class RecommendCommandLog {

    public enum Status {
        REQUESTED,
        SUCCESS,
        FAILED
    }

    private final Long userId;
    private final String policyVersion;
    private final String creditMetaVersion;
    private final String financialMetaVersion;

    private final Status status;
    private final String errorType;
    private final String errorMessage;

    private final Long recommendationId;

    private final LocalDateTime requestedAt;
    private final LocalDateTime finishedAt;

    private RecommendCommandLog(
            Long userId,
            String policyVersion,
            String creditMetaVersion,
            String financialMetaVersion,
            Status status,
            String errorType,
            String errorMessage,
            Long recommendationId,
            LocalDateTime requestedAt,
            LocalDateTime finishedAt
    ) {
        this.userId = userId;
        this.policyVersion = policyVersion;
        this.creditMetaVersion = creditMetaVersion;
        this.financialMetaVersion = financialMetaVersion;
        this.status = status;
        this.errorType = errorType;
        this.errorMessage = errorMessage;
        this.recommendationId = recommendationId;
        this.requestedAt = requestedAt;
        this.finishedAt = finishedAt;
    }

    public static RecommendCommandLog requested(RecommendCommand command) {
        return new RecommendCommandLog(
                command.getUserId(),
                command.getPolicyVersion(),
                command.getCreditMetaVersion(),
                command.getFinancialMetaVersion(),
                Status.REQUESTED,
                null,
                null,
                null,
                command.getRequestedAt(),
                null
        );
    }

    public RecommendCommandLog success(Long recommendationId, LocalDateTime finishedAt) {
        return new RecommendCommandLog(
                this.userId,
                this.policyVersion,
                this.creditMetaVersion,
                this.financialMetaVersion,
                Status.SUCCESS,
                null,
                null,
                recommendationId,
                this.requestedAt,
                finishedAt
        );
    }

    public RecommendCommandLog failed(Throwable t, LocalDateTime finishedAt) {
        return new RecommendCommandLog(
                this.userId,
                this.policyVersion,
                this.creditMetaVersion,
                this.financialMetaVersion,
                Status.FAILED,
                t == null ? null : t.getClass().getSimpleName(),
                t == null ? null : safeMessage(t.getMessage()),
                null,
                this.requestedAt,
                finishedAt
        );
    }

    public Long getUserId() {
        return userId;
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

    public String getErrorType() {
        return errorType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Long getRecommendationId() {
        return recommendationId;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    private static String safeMessage(String msg) {
        if (msg == null) return null;
        // 과도한 길이/민감정보 노출 방지(상세는 audit 로그에서 관리)
        return msg.length() > 500 ? msg.substring(0, 500) : msg;
    }
}
