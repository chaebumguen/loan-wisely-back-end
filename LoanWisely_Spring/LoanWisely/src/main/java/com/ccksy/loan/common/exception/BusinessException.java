package com.ccksy.loan.common.exception;

import lombok.Getter;

/**
 * 비즈니스 규칙 위반 시 사용하는 예외
 *
 * - 도메인/서비스 계층에서만 사용
 * - 단순 시스템 오류가 아닌 "의미 있는 실패"를 표현
 * - 반드시 ErrorCode를 포함한다
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }
}
