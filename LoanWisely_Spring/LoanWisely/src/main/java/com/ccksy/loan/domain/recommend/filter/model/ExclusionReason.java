// FILE: domain/recommend/filter/model/ExclusionReason.java
package com.ccksy.loan.domain.recommend.filter.model;

import java.util.Objects;

/**
 * 제외 사유 표준화(v1).
 *
 * 목적:
 * - filter/chain 에서 제외 판단 시 "reasonCode"를 표준화하여 Explain/Evidence에 연결
 * - 사용자 노출 메시지(userMessage)와 감사 상세(auditDetail)를 분리 저장할 수 있도록 구조 제공
 *
 * v1 규칙:
 * - 이 객체는 "데이터 운반"만 수행 (문구 생성/정책 해석 로직 금지)
 */
public final class ExclusionReason {

    private final String reasonCode;   // 예: RECO_EXCLUDE_DSR_TOO_HIGH
    private final String userMessage;  // 사용자 노출(일반화)
    private final String auditDetail;  // 감사 상세(필요 시)

    private ExclusionReason(String reasonCode, String userMessage, String auditDetail) {
        this.reasonCode = requireText(reasonCode, "reasonCode");
        this.userMessage = requireText(userMessage, "userMessage");
        this.auditDetail = auditDetail == null ? null : auditDetail.trim();
    }

    public static ExclusionReason of(String reasonCode, String userMessage) {
        return new ExclusionReason(reasonCode, userMessage, null);
    }

    public static ExclusionReason of(String reasonCode, String userMessage, String auditDetail) {
        return new ExclusionReason(reasonCode, userMessage, auditDetail);
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public String getAuditDetail() {
        return auditDetail;
    }

    private static String requireText(String v, String field) {
        Objects.requireNonNull(v, field);
        String s = v.trim();
        if (s.isEmpty()) throw new IllegalArgumentException(field + " must not be blank");
        return s;
    }
}
