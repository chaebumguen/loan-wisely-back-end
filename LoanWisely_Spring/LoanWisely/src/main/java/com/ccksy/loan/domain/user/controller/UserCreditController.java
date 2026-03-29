package com.ccksy.loan.domain.user.controller;

import com.ccksy.loan.common.response.ApiResponse;
import com.ccksy.loan.common.security.UserAuthUtil;
import com.ccksy.loan.domain.user.dto.request.UserCreditLv1Request;
import com.ccksy.loan.domain.user.dto.request.UserCreditLv2Request;
import com.ccksy.loan.domain.user.dto.request.UserCreditLv3Request;
import com.ccksy.loan.domain.user.dto.response.UserCreditLv1Response;
import com.ccksy.loan.domain.user.dto.response.UserCreditLv2Response;
import com.ccksy.loan.domain.user.dto.response.UserCreditLv3Response;
import com.ccksy.loan.domain.user.service.UserCreditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/users/me/credit")
@RequiredArgsConstructor
public class UserCreditController {

    private final UserCreditService userCreditService;

    @PutMapping("/lv1")
    public ApiResponse<UserCreditLv1Response> upsertLv1(
            Authentication authentication,
            @Valid @RequestBody UserCreditLv1Request request
    ) {
        Long userId = UserAuthUtil.requireUserId(authentication);
        request.setUserId(userId);
        return ApiResponse.ok(userCreditService.upsertLv1(request));
    }

    @PutMapping("/lv2")
    public ApiResponse<UserCreditLv2Response> upsertLv2(
            Authentication authentication,
            @Valid @RequestBody UserCreditLv2Request request
    ) {
        Long userId = UserAuthUtil.requireUserId(authentication);
        request.setUserId(userId);
        return ApiResponse.ok(userCreditService.upsertLv2(request));
    }

    @PutMapping("/lv3")
    public ApiResponse<UserCreditLv3Response> upsertLv3(
            Authentication authentication,
            @Valid @RequestBody UserCreditLv3Request request
    ) {
        Long userId = UserAuthUtil.requireUserId(authentication);
        request.setUserId(userId);
        return ApiResponse.ok(userCreditService.upsertLv3(request));
    }
}
