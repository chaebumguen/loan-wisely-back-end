// FILE: domain/user/controller/UserProfileController.java
package com.ccksy.loan.domain.user.controller;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ccksy.loan.domain.user.dto.response.UserProfileResponse;
import com.ccksy.loan.domain.user.service.UserProfileService;

/**
 * UserProfileController (v1)
 *
 * 책임:
 * - HTTP 요청 수신/응답 반환 (표현 계층)
 * - 인증 컨텍스트에서 userId 해석
 * - Service로 위임
 *
 * v1 원칙:
 * - Controller는 판단/정책/추천 로직 금지
 * - Request DTO는 "쓰기" 유스케이스에서만 사용(현재는 조회만 제공)
 */
@RestController
@RequestMapping("/api/v1/user/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = Objects.requireNonNull(userProfileService, "userProfileService");
    }

    /**
     * 사용자 프로필 조회
     */
    @GetMapping
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        Long userId = resolveUserId();
        UserProfileResponse response = userProfileService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 인증 컨텍스트에서 userId 해석 (v1)
     *
     * 규약:
     * - principal이 Number면 그대로 사용
     * - 아니면 principal.toString()이 Long 파싱 가능해야 함
     *
     * NOTE:
     * - 프로젝트에 커스텀 UserDetails가 있으면 여기에서 캐스팅하도록 확장
     */
    private Long resolveUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("Unauthenticated request (no authentication principal).");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof Number) {
            return ((Number) principal).longValue();
        }

        String s = String.valueOf(principal).trim();
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot resolve userId from principal: " + principal);
        }
    }
}
