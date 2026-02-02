package com.ccksy.loan.domain.user.service;

import com.ccksy.loan.domain.user.dto.request.UserProfileRequest;
import com.ccksy.loan.domain.user.dto.response.UserProfileResponse;
import com.ccksy.loan.domain.user.entity.UserProfile;
import com.ccksy.loan.domain.user.mapper.UserProfileMapper;
import com.ccksy.loan.domain.user.state.UserState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileMapper userProfileMapper;
    private final UserState userState;

    public UserProfileServiceImpl(
            UserProfileMapper userProfileMapper,
            UserState userState
    ) {
        this.userProfileMapper = userProfileMapper;
        this.userState = userState;
    }

    @Override
    @Transactional
    public UserProfileResponse upsert(UserProfileRequest request) {
        // 상태 전이/입력 허용 여부 판단은 State 객체에 위임
        userState.handle(request);

        UserProfile profile = new UserProfile();
        // 값 매핑 (판단/분기 없음)
        // MyBatis 매핑 기준으로 필드 설정
        // 예: profile.setAge(request.getAge()) 등

        userProfileMapper.insert(profile);

        return toResponse(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse get(Long versionId) {
        UserProfile profile =
                (versionId == null)
                        ? userProfileMapper.selectLatestValidByUserId(getCurrentUserId())
                        : userProfileMapper.selectByUserIdAndVersion(getCurrentUserId(), versionId);

        return toResponse(profile);
    }

    private UserProfileResponse toResponse(UserProfile profile) {
        return new UserProfileResponse(
                profile.getProfileVersionId(),
                profile.getAge(),
                profile.getIncomeYear(),
                profile.getGender(),
                profile.getEmploymentType(),
                profile.getResidenceType(),
                profile.getLoanPurpose(),
                profile.getTotalDebt(),
                profile.getExistingLoanCount(),
                profile.getCreatedAt(),
                profile.isJudgable()
        );
    }

    private Long getCurrentUserId() {
        // 인증 컨텍스트에서 추출 (구현 위치 고정)
        return 0L;
    }
}
