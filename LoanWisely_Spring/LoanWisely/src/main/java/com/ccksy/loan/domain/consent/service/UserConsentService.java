//package com.ccksy.loan.domain.consent.service;
//
//import com.ccksy.loan.domain.consent.dto.request.UserConsentRequest;
//import com.ccksy.loan.domain.consent.dto.response.UserConsentResponse;
//
//import java.util.List;
//
//public interface UserConsentService {
//    UserConsentResponse createConsent(Long userId, UserConsentRequest request);
//    List<UserConsentResponse> getActiveConsents(Long userId);
//}


// FILE: domain/consent/service/UserConsentService.java
package com.ccksy.loan.domain.consent.service;

/**
 * UserConsentService (v1)
 *
 * 역할:
 * - 사용자 동의(consent) 도메인 유스케이스 계약 정의
 */
public interface UserConsentService {

    /**
     * 사용자의 특정 동의 여부 조회
     *
     * @param userId 사용자 ID
     * @param consentType 동의 유형 코드
     * @return 동의 여부
     */
    boolean hasConsent(Long userId, String consentType);

    /**
     * 사용자 동의 등록/갱신
     *
     * @param userId 사용자 ID
     * @param consentType 동의 유형 코드
     * @param agreed 동의 여부
     */
    void saveConsent(Long userId, String consentType, boolean agreed);
}
