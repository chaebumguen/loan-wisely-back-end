package com.ccksy.loan.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Version 1 ErrorCode
 *
 * 원칙:
 * - 오류 응답 일관성(75)
 * - 내부 상태/스택/SQL 등 노출 금지(74)
 * - 도메인별로 prefix를 두고, 코드는 고정(append-only 정신에 맞게 "의미 변경" 금지)
 */
public enum ErrorCode {

    // =========================
    // COMMON
    // =========================
    COMMON_BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON-400", "잘못된 요청입니다."),
    COMMON_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "COMMON-400-VALIDATION", "요청값이 올바르지 않습니다."),
    COMMON_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON-401", "인증이 필요합니다."),
    COMMON_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON-403", "접근 권한이 없습니다."),
    COMMON_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON-404", "대상을 찾을 수 없습니다."),
    COMMON_CONFLICT(HttpStatus.CONFLICT, "COMMON-409", "요청이 충돌했습니다."),
    COMMON_TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "COMMON-429", "요청이 너무 많습니다."),
    COMMON_INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-500", "서버 오류가 발생했습니다."),

    // =========================
    // AUTH / SECURITY
    // =========================
    AUTH_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-401-TOKEN", "유효하지 않은 토큰입니다."),
    AUTH_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-401-EXPIRED", "만료된 토큰입니다."),

    // =========================
    // USER
    // =========================
    USER_PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-404-PROFILE", "사용자 프로필을 찾을 수 없습니다."),
    USER_STATUS_INVALID(HttpStatus.BAD_REQUEST, "USER-400-STATUS", "유효하지 않은 사용자 상태입니다."),

    // =========================
    // CONSENT
    // =========================
    CONSENT_REQUIRED(HttpStatus.BAD_REQUEST, "CONSENT-400-REQUIRED", "동의가 필요합니다."),
    CONSENT_NOT_EFFECTIVE(HttpStatus.BAD_REQUEST, "CONSENT-400-NOT-EFFECTIVE", "유효한 동의가 아닙니다."),

    // =========================
    // METADATA
    // =========================
    METADATA_VERSION_NOT_FOUND(HttpStatus.NOT_FOUND, "META-404-VERSION", "메타 버전을 찾을 수 없습니다."),
    METADATA_NOT_APPROVED(HttpStatus.CONFLICT, "META-409-NOT-APPROVED", "승인되지 않은 메타 버전입니다."),
    METADATA_NOT_ACTIVE(HttpStatus.CONFLICT, "META-409-NOT-ACTIVE", "활성 메타가 아닙니다."),

    // =========================
    // ENGINE / RECOMMENDATION
    // =========================
    ENGINE_PRECONDITION_FAILED(HttpStatus.CONFLICT, "ENGINE-409-PRECONDITION", "판단 전제조건이 충족되지 않았습니다."),
    ENGINE_DETERMINISM_VIOLATION(HttpStatus.CONFLICT, "ENGINE-409-DETERMINISM", "결정론 조건 위반이 감지되었습니다."),
    RECO_NOT_READY(HttpStatus.CONFLICT, "RECO-409-NOT-READY", "추천 실행에 필요한 입력이 부족합니다."),
    RECO_RESULT_NOT_FOUND(HttpStatus.NOT_FOUND, "RECO-404-RESULT", "추천 결과를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
