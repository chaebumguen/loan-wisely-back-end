// FILE: domain/consent/service/UserConsentServiceImpl.java
package com.ccksy.loan.domain.consent.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import com.ccksy.loan.domain.consent.entity.UserConsent;
import com.ccksy.loan.domain.consent.mapper.UserConsentMapper;

/**
 * UserConsentService 援ы쁽泥?(v2)
 *
 * - DB append-only ?대젰 ?곸옱
 * - ?꾩옱 ?좏슚 ?숈쓽만 議고쉶
 */
@Service
public class UserConsentServiceImpl implements UserConsentService {

    private static final long DEFAULT_EXPIRE_DAYS = 365L;

    private final UserConsentMapper userConsentMapper;

    public UserConsentServiceImpl(UserConsentMapper userConsentMapper) {
        this.userConsentMapper = Objects.requireNonNull(userConsentMapper, "userConsentMapper");
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasConsent(Long userId, String consentType) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(consentType, "consentType must not be null");

        List<UserConsent> active = userConsentMapper.findActiveByUserId(userId);
        return active.stream()
                .anyMatch(c -> consentType.equalsIgnoreCase(String.valueOf(c.getConsentTypeCodeValueId())));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveConsent(Long userId, String consentType, boolean agreed) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(consentType, "consentType must not be null");

        UserConsent entity = new UserConsent();
        entity.setUserId(userId);
        entity.setConsentTypeCodeValueId(consentType);
        entity.setAgreedYn(agreed ? "Y" : "N");
        entity.setAgreedAt(Instant.now());
        entity.setExpiredAt(Instant.now().plus(DEFAULT_EXPIRE_DAYS, ChronoUnit.DAYS));
        entity.setIsActive("Y");

        int inserted = userConsentMapper.insert(entity);
        if (inserted <= 0) {
            throw new BusinessException(ErrorCode.COMMON_INTERNAL_ERROR);
        }
    }
}