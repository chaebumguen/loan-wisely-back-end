package com.ccksy.loan.domain.consent.dto.response;

import com.ccksy.loan.domain.consent.entity.UserConsent;

import java.time.Instant;

public class UserConsentResponse {

    private Long consentId;
    private Long userId;
    private String level;
    private boolean agreed;
    private Instant agreedAt;
    private Instant expiredAt;

    /**
     * 목적태그 JSON 파일 경로(요청사항: DB에 JSON 저장 금지, 경로 저장)
     */
    private String purposeTagsPath;

    public static UserConsentResponse from(UserConsent e) {
        UserConsentResponse r = new UserConsentResponse();
        r.consentId = e.getConsentId();
        r.userId = e.getUserId();
        r.level = e.getConsentTypeCodeValueId();
        r.agreed = "Y".equalsIgnoreCase(e.getAgreedYn());
        r.agreedAt = e.getAgreedAt();
        r.expiredAt = e.getExpiredAt();
        r.purposeTagsPath = e.getPurposeTagsPath();
        return r;
    }

    public Long getConsentId() { return consentId; }
    public Long getUserId() { return userId; }
    public String getLevel() { return level; }
    public boolean isAgreed() { return agreed; }
    public Instant getAgreedAt() { return agreedAt; }
    public Instant getExpiredAt() { return expiredAt; }
    public String getPurposeTagsPath() { return purposeTagsPath; }
}
