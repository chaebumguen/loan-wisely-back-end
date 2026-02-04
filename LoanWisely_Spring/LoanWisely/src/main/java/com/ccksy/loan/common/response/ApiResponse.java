package com.ccksy.loan.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * ??API 怨듯넻 ?묐떟 Wrapper
 *
 * ?ㅺ퀎 ?먯튃:
 * - 紐⑤뱺 API???숈씪???묐떟 援ъ“瑜?媛吏꾨떎
 * - ?깃났/?ㅽ뙣 ?щ?瑜?payload ?몃??먯꽌 紐낇솗??援щ텇
 * - ?대? ?곹깭/濡쒖쭅/?ㅽ깮?몃젅?댁뒪 ?덈? ?몄텧 湲덉?
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

    /** ?깃났 ?묐떟 */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    /** ?ㅽ뙣 ?묐떟(湲곕낯) */
    public static <T> ApiResponse<T> failure(String code, String message) {
        return new ApiResponse<>(false, null, new ErrorResponse(code, message, null, null));
    }

    /**
     * ?ㅽ뙣 ?묐떟(?곸꽭 ?ы븿) - ?? ?꾨뱶 寃利??ㅻ쪟 Map ??
     * - rejectedValue, stackTrace, SQL ???대?媛믪? ?덈? ?댁? 留덉꽭??
     */
    public static ApiResponse<Object> failure(String code, String message, Object details) {
        return new ApiResponse<>(false, null, new ErrorResponse(code, message, details, null));
    }

    /** ??쎈솭 ?臾먮뼗(?怨멸쉭 + traceId) */
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
     * ?ㅻ쪟 ?뺣낫 理쒖냼 ?⑥쐞
     * - ?대? ?덉쇅, ?대옒?ㅻ챸, SQL, StackTrace ?덈? ?ы븿 湲덉?
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorResponse {

        private final String code;
        private final String message;
        private final Object details; // ?좏깮: ?꾨뱶 ?먮윭 ???대?媛?湲덉?)
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



