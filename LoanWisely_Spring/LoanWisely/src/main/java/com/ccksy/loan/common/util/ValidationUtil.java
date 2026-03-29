package com.ccksy.loan.common.util;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;

import java.math.BigDecimal;

public final class ValidationUtil {

    private ValidationUtil() {
    }

    public static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, fieldName + " 값이 필요합니다.");
        }
    }

    public static void requirePositive(BigDecimal value, String fieldName) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, fieldName + " 값은 0보다 커야 합니다.");
        }
    }

    public static void requireRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST,
                    String.format("%s 값은 %d~%d 범위여야 합니다.", fieldName, min, max)
            );
        }
    }
}
