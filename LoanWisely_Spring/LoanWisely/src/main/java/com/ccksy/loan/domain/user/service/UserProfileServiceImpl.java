// FILE: domain/user/service/UserProfileServiceImpl.java
package com.ccksy.loan.domain.user.service;

import java.math.BigDecimal;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import com.ccksy.loan.domain.user.dto.response.UserProfileResponse;
import com.ccksy.loan.domain.user.entity.UserProfile;
import com.ccksy.loan.domain.user.mapper.UserProfileMapper;

/**
 * UserProfileService 援ы쁽泥?(v2)
 *
 * - ?쒖젏湲곗? 최신 유효 프로필 조회
 * - 존재하지 않을 경우 404 반환
 */
@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileMapper userProfileMapper;

    public UserProfileServiceImpl(UserProfileMapper userProfileMapper) {
        this.userProfileMapper = Objects.requireNonNull(userProfileMapper, "userProfileMapper");
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId) {
        Objects.requireNonNull(userId, "userId must not be null.");

        UserProfile profile = userProfileMapper.selectLatestValidByUserId(userId);
        if (profile == null) {
            throw new BusinessException(ErrorCode.USER_PROFILE_NOT_FOUND);
        }

        return toResponse(profile);
    }

    private UserProfileResponse toResponse(UserProfile profile) {
        UserProfileResponse resp = new UserProfileResponse();
        resp.setUserId(profile.getUserId());
        resp.setAge(profile.getAge());
        if (profile.getIncomeYear() != null) {
            resp.setAnnualIncome(BigDecimal.valueOf(profile.getIncomeYear()));
        }
        resp.setGender(profile.getGender());
        resp.setEmploymentType(profile.getEmploymentType());
        resp.setResidenceType(profile.getResidenceType());
        resp.setLoanPurpose(profile.getLoanPurpose());
        if (profile.getTotalDebt() != null) {
            resp.setTotalDebtAmount(BigDecimal.valueOf(profile.getTotalDebt()));
        }
        resp.setExistingLoanCount(profile.getExistingLoanCount());
        return resp;
    }
}