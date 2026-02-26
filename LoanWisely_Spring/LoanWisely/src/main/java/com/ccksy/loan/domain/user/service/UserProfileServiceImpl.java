package com.ccksy.loan.domain.user.service;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import com.ccksy.loan.domain.user.dto.request.UserProfileRequest;
import com.ccksy.loan.domain.user.dto.response.UserProfileResponse;
import com.ccksy.loan.domain.user.entity.UserProfile;
import com.ccksy.loan.domain.user.mapper.UserProfileMapper;
import com.ccksy.loan.domain.user.state.BeforeInputState;
import com.ccksy.loan.domain.user.state.InputCompletedState;
import com.ccksy.loan.domain.user.state.NotJudgableState;
import com.ccksy.loan.domain.user.state.UserInputState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileMapper userProfileMapper;

    @Override
    @Transactional
    public UserProfileResponse upsertProfile(UserProfileRequest request) {
        // 최소 검증(엄격): userId/LV는 필수, LV1 필수 필드 누락이면 차단
        request.assertRequiredFields();

        // 신규 이력 엔티티 생성 (DB에서 created_at 등 처리 가능)
        UserProfile entity = UserProfile.from(request);

        // 상태 계산 (결정론 유지: 동일 입력이면 동일 상태)
        UserInputState state = determineState(entity);
        entity = entity.toBuilder()
                .inputStateCode(state.code())
                .createdAt(LocalDateTime.now())
                .isActive("Y")
                .build();

        // 이력 불변 원칙: 기존 유효 레코드는 비활성 처리 후 신규 insert
        userProfileMapper.deactivateActiveByUserId(entity.getUserId());
        userProfileMapper.insertUserProfile(entity);

        UserProfile latest = userProfileMapper.selectLatestActiveByUserId(entity.getUserId());
        if (latest == null) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "프로필 저장 후 최신 조회에 실패했습니다.");
        }
        return UserProfileResponse.from(latest);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getLatestProfile(Long userId) {
        UserProfile latest = userProfileMapper.selectLatestActiveByUserId(userId);
        if (latest == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "해당 사용자의 프로필이 존재하지 않습니다.");
        }
        return UserProfileResponse.from(latest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileResponse> getProfileHistory(Long userId) {
        List<UserProfile> history = userProfileMapper.selectHistoryByUserId(userId);
        return history.stream().map(UserProfileResponse::from).collect(Collectors.toList());
    }

    /**
     * 상태 판정 규칙 (State 패턴)
     * - LV1 필수 입력이 모두 있으면 InputCompleted
     * - 그 외는 BeforeInput
     * - 유효성 위반(값 범위 등)이 명백하면 NotJudgable (현재는 최소 규칙만 반영)
     */
    private UserInputState determineState(UserProfile entity) {
        // 값 범위 예시(확장 가능): age < 0 등
        if (entity.getAge() != null && entity.getAge() < 0) {
            return new NotJudgableState();
        }

        boolean hasLv1Required =
                entity.getInputLevel() != null &&
                entity.getAge() != null &&
                entity.getIncomeYear() != null &&
                entity.getGender() != null;

        if (!hasLv1Required) {
            return new BeforeInputState();
        }
        return new InputCompletedState();
    }
}
