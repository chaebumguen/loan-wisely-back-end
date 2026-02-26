package com.ccksy.loan.domain.user.auth;

import com.ccksy.loan.common.response.ApiResponse;
import com.ccksy.loan.common.security.UserAuthUtil;
import com.ccksy.loan.domain.user.auth.dto.UserLoginRequest;
import com.ccksy.loan.domain.user.auth.dto.UserLoginResponse;
import com.ccksy.loan.domain.user.auth.dto.UserRegisterRequest;
import com.ccksy.loan.domain.user.auth.dto.UserRegisterResponse;
import com.ccksy.loan.domain.user.auth.dto.UserVerifyResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserAuthController {

    private final UserAuthService userAuthService;

    public UserAuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @PostMapping("/login")
    public ApiResponse<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        return ApiResponse.ok(userAuthService.login(request));
    }

    @PostMapping("/register")
    public ApiResponse<UserRegisterResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        return ApiResponse.ok(userAuthService.register(request));
    }

    @PostMapping("/verify")
    public ApiResponse<UserVerifyResponse> verify(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ApiResponse.<UserVerifyResponse>fail("C002", "Missing Authorization header", null);
        }
        String token = authHeader.replace("Bearer ", "").trim();
        return ApiResponse.ok(userAuthService.verify(token));
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(Authentication authentication) {
        UserAuthUtil.requireUserId(authentication);
        return ApiResponse.ok("OK");
    }
}
