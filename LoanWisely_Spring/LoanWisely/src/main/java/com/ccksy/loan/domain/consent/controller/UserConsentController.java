package com.ccksy.loan.domain.consent.controller;

import com.ccksy.loan.common.response.ApiResponse;
import com.ccksy.loan.common.security.UserAuthUtil;
import com.ccksy.loan.domain.consent.dto.request.UserConsentRequest;
import com.ccksy.loan.domain.consent.dto.response.UserConsentResponse;
import com.ccksy.loan.domain.consent.service.UserConsentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/users/me/consents")
@RequiredArgsConstructor
public class UserConsentController {

    private final UserConsentService userConsentService;

    /**
     * (설계서 기준) 사용자 동의 저장
     */
    @PostMapping
    public ApiResponse<UserConsentResponse> upsert(
            Authentication authentication,
            @Valid @RequestBody UserConsentRequest request
    ) {
        Long userId = UserAuthUtil.requireUserId(authentication);
        request.setUserId(userId);
        return ApiResponse.ok(userConsentService.upsert(request));
    }

    /**
     * (설계서 기준) 사용자 유효 동의 조회
     */
    @GetMapping
    public ApiResponse<List<UserConsentResponse>> getActiveConsents(
            Authentication authentication
    ) {
        Long userId = UserAuthUtil.requireUserId(authentication);
        return ApiResponse.ok(userConsentService.getActiveConsents(userId));
    }
}
