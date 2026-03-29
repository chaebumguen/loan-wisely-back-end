package com.ccksy.loan.domain.admin.auth;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import com.ccksy.loan.common.response.ApiResponse;
import com.ccksy.loan.common.security.AdminTokenClaims;
import com.ccksy.loan.domain.admin.auth.dto.AdminLoginRequest;
import com.ccksy.loan.domain.admin.auth.dto.AdminLoginResponse;
import com.ccksy.loan.domain.admin.auth.dto.AdminVerifyResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    private final AdminAuthService authService;

    public AdminAuthController(AdminAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @PostMapping("/verify")
    public ApiResponse<AdminVerifyResponse> verify(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AdminTokenClaims claims)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Unauthorized");
        }
        return ApiResponse.ok(authService.verify(claims));
    }
}
