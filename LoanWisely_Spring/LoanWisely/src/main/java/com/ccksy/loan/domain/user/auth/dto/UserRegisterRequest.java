package com.ccksy.loan.domain.user.auth.dto;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRegisterRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public void assertRequiredFields() {
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "username is required");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "password is required");
        }
    }
}
