package com.ccksy.loan.common.security;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import org.springframework.security.core.Authentication;

public final class UserAuthUtil {

    private UserAuthUtil() {}

    public static Long requireUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserTokenClaims claims)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Unauthorized");
        }
        if (claims.userId() == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Unauthorized");
        }
        return claims.userId();
    }
}
