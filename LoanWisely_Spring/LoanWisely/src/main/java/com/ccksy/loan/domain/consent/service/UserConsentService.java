package com.ccksy.loan.domain.consent.service;

import com.ccksy.loan.domain.consent.dto.request.UserConsentRequest;
import com.ccksy.loan.domain.consent.dto.response.UserConsentResponse;

import java.util.List;

public interface UserConsentService {
    UserConsentResponse createConsent(Long userId, UserConsentRequest request);
    List<UserConsentResponse> getActiveConsents(Long userId);
}
