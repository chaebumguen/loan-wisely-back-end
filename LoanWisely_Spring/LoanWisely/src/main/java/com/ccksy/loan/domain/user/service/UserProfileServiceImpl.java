// FILE: domain/user/service/UserProfileServiceImpl.java
package com.ccksy.loan.domain.user.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccksy.loan.domain.user.dto.response.UserProfileResponse;

/**
 * UserProfileService 구현체 (v1)
 *
 * v1 정책 반영:
 * - LV1 필드는 항상 반환 대상
 * - LV2/LV3는 저장/동의/정책 상태에 따라 null일 수 있음
 * - Response DTO에는 예외를 던지지 않음
 *
 * NOTE:
 * - 실제 데이터 조회는 Mapper/Repository로 교체되어야 함
 * - v1에서는 구조/책임 정합에 집중
 */
@Service
public class UserProfileServiceImpl implements UserProfileService {

    public UserProfileServiceImpl() {
        // 실제 구현에서는 UserProfileMapper / Repository 주입
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId) {
        Objects.requireNonNull(userId, "userId must not be null.");

        // TODO: 실제 조회 로직은 영속계층으로 대체
        // v1 기본 동작: 조회 결과가 없더라도 Response 계약은 유지
        UserProfileResponse response = new UserProfileResponse();
        response.setUserId(userId);

        // LV1/LV2/LV3는 정책/동의 상태에 따라 세팅될 수 있음
        // (여기서는 예시로 null 유지)

        return response;
    }
}
