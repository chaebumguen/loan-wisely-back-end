package com.ccksy.loan.common.exception;

import lombok.Getter;

/**
 * 입력값 검증 실패 시 사용하는 예외
 *
 * - DTO 검증(@Valid) 이후
 * - 비즈니스 로직 진입 전/중 검증 실패 표현
 * - 컨트롤러에서는 절대 처리하지 않는다
 */
@Getter
public class ValidationException extends RuntimeException {

    private final ErrorCode errorCode;

    public ValidationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ValidationException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }
}
