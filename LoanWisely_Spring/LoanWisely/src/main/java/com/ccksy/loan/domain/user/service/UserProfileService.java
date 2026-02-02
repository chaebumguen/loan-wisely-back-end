package com.ccksy.loan.domain.user.service;

import com.ccksy.loan.domain.user.dto.request.UserProfileRequest;
import com.ccksy.loan.domain.user.dto.response.UserProfileResponse;

public interface UserProfileService {

    /**
     * 사용자 프로필 저장 (append-only)
     */
    UserProfileResponse upsert(UserProfileRequest request);

    /**
     * 사용자 프로필 조회
     * - versionId == null : 최신 유효 이력
     * - versionId != null : 특정 이력 (감사용)
     */
    UserProfileResponse get(Long versionId);
}
