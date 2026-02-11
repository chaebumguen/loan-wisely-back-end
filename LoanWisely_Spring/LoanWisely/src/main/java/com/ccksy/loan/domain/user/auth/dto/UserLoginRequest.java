package com.ccksy.loan.domain.user.auth.dto;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserLoginRequest {
    private String username;
    private String password;

    public void assertRequiredFields() {
        if (username == null || username.isBlank()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "username is required");
        }
        if (password == null || password.isBlank()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "password is required");
        }
    }
}
