package com.ccksy.loan.domain.consent.dto.response;

import com.ccksy.loan.domain.consent.entity.UserConsent;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserConsentResponse {

    private Long userId;
    private Integer consentLevel;
    private String consentGiven; // "Y"/"N"

    private String isActive;     // "Y"/"N"
    private LocalDateTime createdAt;

    public static UserConsentResponse from(UserConsent entity) {
        return UserConsentResponse.builder()
                .userId(entity.getUserId())
                .consentLevel(entity.getConsentLevel())
                .consentGiven(entity.getConsentGiven())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
