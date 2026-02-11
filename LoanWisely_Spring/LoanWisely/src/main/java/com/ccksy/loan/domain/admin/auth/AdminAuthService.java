package com.ccksy.loan.domain.admin.auth;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import com.ccksy.loan.common.security.AdminJwtService;
import com.ccksy.loan.common.security.AdminTokenClaims;
import com.ccksy.loan.domain.admin.auth.dto.AdminLoginRequest;
import com.ccksy.loan.domain.admin.auth.dto.AdminLoginResponse;
import com.ccksy.loan.domain.admin.auth.dto.AdminVerifyResponse;
import com.ccksy.loan.domain.admin.auth.entity.AdminUser;
import com.ccksy.loan.domain.admin.auth.mapper.AdminUserMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminAuthService {

    private final AdminJwtService jwtService;
    private final long ttlSeconds;
    private final AdminUserMapper adminUserMapper;

    public AdminAuthService(
            AdminJwtService jwtService,
            @Value
            ("${security.admin-jwt-ttl-secs}") long ttlSeconds,
            AdminUserMapper adminUserMapper
    ) {
        this.jwtService = jwtService;
        this.ttlSeconds = ttlSeconds;
        this.adminUserMapper = adminUserMapper;
    }

    public AdminLoginResponse login(AdminLoginRequest request) {
        AdminUser user = adminUserMapper.selectByUsername(request.getUsername());
        if (user == null || !"ACTIVE".equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Invalid credentials");
        }
        if (!matchesPassword(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Invalid credentials");
        }
        List<String> roles = adminUserMapper.selectRolesByAdminId(user.getAdminId());
        String token = jwtService.issueToken(user.getUsername(), roles);
        return new AdminLoginResponse(token, ttlSeconds, user.getUsername(), "ADMIN");
    }

    public AdminVerifyResponse verify(AdminTokenClaims claims) {
        return new AdminVerifyResponse(claims.adminId(), claims.roles());
    }

    private boolean matchesPassword(String raw, String stored) {
        if (stored == null) {
            return false;
        }
        if (stored.startsWith("plain:")) {
            return stored.substring("plain:".length()).equals(raw);
        }
        return stored.equals(raw);
    }
}
