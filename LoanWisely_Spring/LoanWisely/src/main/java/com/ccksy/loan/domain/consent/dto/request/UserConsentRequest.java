package com.ccksy.loan.domain.consent.dto.request;

import java.util.List;

public class UserConsentRequest {

    /**
     * API 명세: level, purposeTags:contentReference[oaicite:12]{index=12}
     * - level 예: LV2, LV3 (프로젝트 코드 체계에 맞게 고정)
     */
    private String level;
    private List<String> purposeTags;

    /**
     * true면 동의, false면 거부(저장 자체는 가능하되 agreed_yn으로 보관)
     */
    private boolean agreedYn = true;

    /**
     * 만료(일): 미지정 시 서버 기본값 적용
     */
    private Integer expireDays;

    public void validate() {
        if (level == null || level.isBlank()) {
            throw new IllegalArgumentException("level is required");
        }
        if (!("LV2".equals(level) || "LV3".equals(level))) {
            throw new IllegalArgumentException("level must be LV2 or LV3");
        }
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<String> getPurposeTags() {
        return purposeTags;
    }

    public void setPurposeTags(List<String> purposeTags) {
        this.purposeTags = purposeTags;
    }

    public boolean isAgreedYn() {
        return agreedYn;
    }

    public void setAgreedYn(boolean agreedYn) {
        this.agreedYn = agreedYn;
    }

    public Integer getExpireDays() {
        return expireDays;
    }

    public void setExpireDays(Integer expireDays) {
        this.expireDays = expireDays;
    }
}
