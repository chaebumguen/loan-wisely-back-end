package com.ccksy.loan.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * API 공통 응답 Wrapper
 *
 * 목적:
 * - 모든 API가 동일한 응답 구조를 사용한다.
 * - success 플래그로 성공/실패를 명확히 구분한다.
 * - 내부 상태/로직/스택트레이스 등 민감 정보는 응답에 노출하지 않는다.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final ErrorResponse error;

    private ApiResponse(boolean success, T data, ErrorResponse error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    /** 성공 응답 */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    /** 실패 응답(기본) */
    public static <T> ApiResponse<T> failure(String code, String message) {
        return new ApiResponse<>(false, null, new ErrorResponse(code, message, null, null));
    }

    /**
     * 실패 응답(상세 포함)
     * - 필드 검증 오류 Map 등 상세 정보를 담을 때 사용
     * - rejectedValue, stackTrace, SQL 등 내부 값은 넣지 않는다.
     */
    public static ApiResponse<Object> failure(String code, String message, Object details) {
        return new ApiResponse<>(false, null, new ErrorResponse(code, message, details, null));
    }

    /** 실패 응답(사용자 메시지 + traceId) */
    public static ApiResponse<Object> failure(String code, String message, Object details, String traceId) {
        return new ApiResponse<>(false, null, new ErrorResponse(code, message, details, traceId));
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public ErrorResponse getError() {
        return error;
    }

    /**
     * 오류 정보 최소 단위
     * - 내부 예외/클래스명/SQL/StackTrace 등은 포함하지 않는다.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorResponse {

        private final String code;
        private final String message;
        private final Object details; // 선택: 필드 오류 등 상세 정보
        private final String traceId;

        private ErrorResponse(String code, String message, Object details, String traceId) {
            this.code = code;
            this.message = message;
            this.details = details;
            this.traceId = traceId;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public Object getDetails() {
            return details;
        }

        public String getTraceId() {
            return traceId;
        }
    }
}



