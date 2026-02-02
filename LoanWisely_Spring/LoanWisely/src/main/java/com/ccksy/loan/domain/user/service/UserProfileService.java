// FILE: domain/user/service/UserProfileService.java
package com.ccksy.loan.domain.user.service;

import com.ccksy.loan.domain.user.dto.response.UserProfileResponse;

/**
 * UserProfileService (v1)
 *
 * 책임:
 * - 사용자 프로필 조회 유스케이스 진입점
 *
 * 원칙:
 * - LV 정책 판단/동의 여부 검증은 구현체에서 수행
 * - DTO는 운반만, 판단은 Service에서
 */
public interface UserProfileService {

    /**
     * 사용자 프로필 조회
     *
     * @param userId 사용자 식별자
     * @return 사용자 프로필 응답
     */
    UserProfileResponse getUserProfile(Long userId);
}
