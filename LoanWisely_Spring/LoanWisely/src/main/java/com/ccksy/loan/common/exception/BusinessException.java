package com.ccksy.loan.common.exception;

/**
 * 도메인/업무 규칙 위반을 표현하는 표준 예외.
 * - ErrorCode 기반으로만 외부 응답 구성
 * - cause는 내부 로그용으로만 사용(노출 금지)
 */
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode != null ? errorCode.getMessage() : null);
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode != null ? errorCode.getMessage() : null, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
