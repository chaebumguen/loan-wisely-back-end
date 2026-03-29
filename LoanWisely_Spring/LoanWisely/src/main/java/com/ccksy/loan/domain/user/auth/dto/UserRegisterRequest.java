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

    private static final int MIN_USERNAME_LENGTH = 6;
    private static final int MIN_PASSWORD_LENGTH = 8;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public void assertRequiredFields() {
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "username is required");
        }
        if (username.trim().length() < MIN_USERNAME_LENGTH) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "username must be at least 6 characters");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "password is required");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "password must be at least 8 characters");
        }
        boolean hasLowercase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(
                ch -> !Character.isLetterOrDigit(ch) && !Character.isWhitespace(ch)
        );
        if (!(hasLowercase && hasDigit && hasSpecial)) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_FAILED,
                    "password must include lowercase letters, numbers, and special characters"
            );
        }
    }
}
