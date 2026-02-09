// FILE: domain/user/controller/UserProfileController.java
package com.ccksy.loan.domain.user.controller;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ccksy.loan.common.security.UserIdResolver;
import com.ccksy.loan.domain.user.dto.response.UserProfileResponse;
import com.ccksy.loan.domain.user.service.UserProfileService;

/**
 * UserProfileController (v1)
 *
 * 책임:
 * - HTTP 요청 수신/응답 반환 (표현 계층)
 * - 인증 컨텍스트에서 userId 추출
 * - Service로 위임
 *
 * v1 원칙:
 * - Controller는 정책/검증/추천 로직 금지
 * - Request DTO는 "읽기" 유스케이스에만 사용(현재는 조회만 제공)
 */
@RestController
@RequestMapping("/api/users/me/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserIdResolver userIdResolver;

    public UserProfileController(UserProfileService userProfileService, UserIdResolver userIdResolver) {
        this.userProfileService = Objects.requireNonNull(userProfileService, "userProfileService");
        this.userIdResolver = Objects.requireNonNull(userIdResolver, "userIdResolver");
    }

    /**
     * 사용자 프로필 조회
     */
    @GetMapping
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        Long userId = userIdResolver.requireUserId();
        UserProfileResponse response = userProfileService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }
}
