package com.ccksy.loan.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // Common
    INTERNAL_ERROR("C000", "서버 내부 오류", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST("C001", "잘못된 요청", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("C002", "인증이 필요합니다", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("C003", "권한이 없습니다", HttpStatus.FORBIDDEN),

    // Validation
    VALIDATION_FAILED("V000", "요청 값 검증 실패", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
