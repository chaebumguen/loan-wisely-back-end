package com.ccksy.loan.domain.consent.service;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import com.ccksy.loan.domain.consent.dto.request.UserConsentRequest;
import com.ccksy.loan.domain.consent.dto.response.UserConsentResponse;
import com.ccksy.loan.domain.consent.entity.UserConsent;
import com.ccksy.loan.domain.consent.mapper.UserConsentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserConsentServiceImpl implements UserConsentService {

    private final UserConsentMapper userConsentMapper;

    @Override
    @Transactional
    public UserConsentResponse upsert(UserConsentRequest request) {
        request.assertRequiredFields();

        UserConsent entity = UserConsent.from(request)
                .toBuilder()
                .isActive("Y")
                .createdAt(LocalDateTime.now())
                .build();

        // 기존 활성 동의 비활성 처리 후 신규 insert
        userConsentMapper.deactivateActiveByUserIdAndLevel(entity.getUserId(), entity.getConsentLevel());
        userConsentMapper.insertUserConsent(entity);

        UserConsent latest = userConsentMapper.selectLatestActiveByUserIdAndLevel(entity.getUserId(), entity.getConsentLevel());
        if (latest == null) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "동의 저장 후 최신 조회에 실패했습니다.");
        }
        return UserConsentResponse.from(latest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserConsentResponse> getActiveConsents(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "userId는 필수입니다.");
        }

        List<UserConsent> list = userConsentMapper.selectActiveByUserId(userId);
        return list.stream().map(UserConsentResponse::from).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserConsentResponse> getHistory(Long userId, Integer consentLevel) {
        validateLevel(consentLevel);

        List<UserConsent> history = userConsentMapper.selectHistoryByUserIdAndLevel(userId, consentLevel);
        return history.stream().map(UserConsentResponse::from).collect(Collectors.toList());
    }

    private void validateLevel(Integer consentLevel) {
        if (consentLevel == null || consentLevel < 1 || consentLevel > 3) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "consentLevel은 1~3 범위여야 합니다.");
        }
    }
}
