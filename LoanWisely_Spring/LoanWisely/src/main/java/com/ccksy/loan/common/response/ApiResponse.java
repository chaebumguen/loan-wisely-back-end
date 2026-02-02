package com.ccksy.loan.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 전 API 공통 응답 Wrapper
 *
 * 설계 원칙:
 * - 모든 API는 동일한 응답 구조를 가진다
 * - 성공/실패 여부를 payload 외부에서 명확히 구분
 * - 내부 상태/로직/스택트레이스 절대 노출 금지
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
    public static ApiResponse<Void> failure(String code, String message) {
        return new ApiResponse<>(false, null, new ErrorResponse(code, message, null));
    }

    /**
     * 실패 응답(상세 포함) - 예: 필드 검증 오류 Map 등
     * - rejectedValue, stackTrace, SQL 등 내부값은 절대 담지 마세요.
     */
    public static ApiResponse<Object> failure(String code, String message, Object details) {
        return new ApiResponse<>(false, null, new ErrorResponse(code, message, details));
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
     * - 내부 예외, 클래스명, SQL, StackTrace 절대 포함 금지
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorResponse {

        private final String code;
        private final String message;
        private final Object details; // 선택: 필드 에러 등(내부값 금지)

        private ErrorResponse(String code, String message, Object details) {
            this.code = code;
            this.message = message;
            this.details = details;
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
    }
}
