package com.ccksy.loan.domain.user.controller;

import com.ccksy.loan.common.response.ApiResponse;
import com.ccksy.loan.common.security.UserAuthUtil;
import com.ccksy.loan.domain.user.dto.request.UserProfileRequest;
import com.ccksy.loan.domain.user.dto.response.UserProfileResponse;
import com.ccksy.loan.domain.user.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * (설계서 기준) 사용자 프로필 생성/갱신
     * - append-only: 신규 row insert, 기존 활성 레코드는 비활성 처리
     */
    @PutMapping("/profile")
    public ApiResponse<UserProfileResponse> upsertProfile(
            Authentication authentication,
            @Valid @RequestBody UserProfileRequest request
    ) {
        Long userId = UserAuthUtil.requireUserId(authentication);
        request.setUserId(userId);
        return ApiResponse.ok(userProfileService.upsertProfile(request));
    }

    /**
     * (설계서 기준) 프로필 조회
     */
    @GetMapping("/profile")
    public ApiResponse<UserProfileResponse> getLatestProfile(
            Authentication authentication
    ) {
        Long userId = UserAuthUtil.requireUserId(authentication);
        return ApiResponse.ok(userProfileService.getLatestProfile(userId));
    }

    /**
     * 프로필 이력 조회 (내부 확인용)
     */
    @GetMapping("/profile/history")
    public ApiResponse<java.util.List<UserProfileResponse>> getProfileHistory(
            Authentication authentication
    ) {
        Long userId = UserAuthUtil.requireUserId(authentication);
        return ApiResponse.ok(userProfileService.getProfileHistory(userId));
    }
}
