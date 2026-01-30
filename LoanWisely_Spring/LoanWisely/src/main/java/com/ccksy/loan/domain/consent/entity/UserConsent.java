package com.ccksy.loan.domain.consent.entity;

import java.time.Instant;

/**
 * ERD: USER_CONSENT(consent_id, user_id, consent_type_code_value_id, agreed_yn, agreed_at, expired_at, ...):contentReference[oaicite:13]{index=13}
 */
public class UserConsent {

    private Long consentId;
    private Long userId;

    // code_value_id 형태로 저장(코드사전 연동 고려)
    private String consentTypeCodeValueId;

    // Y/N
    private String agreedYn;

    private Instant agreedAt;
    private Instant expiredAt;

    // JSON은 DB 저장 금지 → 파일 경로로 저장
    private String purposeTagsPath;

    // Y/N
    private String isActive;

    public Long getConsentId() { return consentId; }
    public void setConsentId(Long consentId) { this.consentId = consentId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getConsentTypeCodeValueId() { return consentTypeCodeValueId; }
    public void setConsentTypeCodeValueId(String consentTypeCodeValueId) { this.consentTypeCodeValueId = consentTypeCodeValueId; }

    public String getAgreedYn() { return agreedYn; }
    public void setAgreedYn(String agreedYn) { this.agreedYn = agreedYn; }

    public Instant getAgreedAt() { return agreedAt; }
    public void setAgreedAt(Instant agreedAt) { this.agreedAt = agreedAt; }

    public Instant getExpiredAt() { return expiredAt; }
    public void setExpiredAt(Instant expiredAt) { this.expiredAt = expiredAt; }

    public String getPurposeTagsPath() { return purposeTagsPath; }
    public void setPurposeTagsPath(String purposeTagsPath) { this.purposeTagsPath = purposeTagsPath; }

    public String getIsActive() { return isActive; }
    public void setIsActive(String isActive) { this.isActive = isActive; }
}
