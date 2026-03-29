package com.ccksy.loan.domain.consent.entity;

import com.ccksy.loan.domain.consent.dto.request.UserConsentRequest;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 사용자 LV별 동의 이력 엔티티
 * - 이력 불변: 업데이트 금지, 신규 이력만 추가
 * - 최신 유효는 is_active='Y'로 식별
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserConsent {

    // PK는 DB 시퀀스/Mapper에서 처리 (필드만 정의)
    private Long consentId;

    private Long userId;

    /**
     * 1~3 (LV)
     */
    private Integer consentLevel;

    /**
     * "Y"/"N"
     */
    private String consentGiven;

    /**
     * "Y"/"N"
     */
    private String isActive;

    private LocalDateTime createdAt;

    public static UserConsent from(UserConsentRequest req) {
        return UserConsent.builder()
                .userId(req.getUserId())
                .consentLevel(req.getConsentLevel())
                .consentGiven(Boolean.TRUE.equals(req.getConsentGiven()) ? "Y" : "N")
                .build();
    }
}
