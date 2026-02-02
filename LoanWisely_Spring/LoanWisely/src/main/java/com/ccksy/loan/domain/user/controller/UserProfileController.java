package com.ccksy.loan.domain.user.controller;

import com.ccksy.loan.common.response.ApiResponse;
import com.ccksy.loan.domain.user.dto.request.UserProfileRequest;
import com.ccksy.loan.domain.user.dto.response.UserProfileResponse;
import com.ccksy.loan.domain.user.service.UserProfileService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PutMapping
    public ApiResponse<UserProfileResponse> upsertProfile(
            @RequestBody UserProfileRequest request
    ) {
        // 입력 검증 실패 시 Exception throw (검증 로직/분기 없음)
        return ApiResponse.success(userProfileService.upsert(request));
    }

    @GetMapping
    public ApiResponse<UserProfileResponse> getProfile(
            @RequestParam(value = "versionId", required = false) Long versionId
    ) {
        return ApiResponse.success(userProfileService.get(versionId));
    }
}
