package com.ccksy.loan.domain.consent.service;

import com.ccksy.loan.domain.consent.dto.request.UserConsentRequest;
import com.ccksy.loan.domain.consent.dto.response.UserConsentResponse;

import java.util.List;

public interface UserConsentService {

    UserConsentResponse upsert(UserConsentRequest request);

    List<UserConsentResponse> getActiveConsents(Long userId);

    List<UserConsentResponse> getHistory(Long userId, Integer consentLevel);
}
