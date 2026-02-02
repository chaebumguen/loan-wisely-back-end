package com.ccksy.loan.common.exception;

import com.ccksy.loan.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Version 1: 표준 오류 응답 제공
 * - 내부 정보 노출 방지(74)
 * - 오류 응답 일관성(75)
 * - 감사/추적을 위한 서버 로그는 남기되, 외부 응답은 최소화
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(ValidationException ex) {
        ErrorCode ec = ex.getErrorCode();
        ApiResponse<Object> body = ApiResponse.failure(ec.getCode(), ec.getMessage(), ex.getFieldErrors());
        return ResponseEntity.status(ec.getHttpStatus()).body(body);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        ErrorCode ec = ex.getErrorCode() != null ? ex.getErrorCode() : ErrorCode.COMMON_INTERNAL_ERROR;
        ApiResponse<Void> body = ApiResponse.failure(ec.getCode(), ec.getMessage());
        return ResponseEntity.status(ec.getHttpStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                fieldErrors.put(err.getField(), err.getDefaultMessage())
        );

        ApiResponse<Object> body = ApiResponse.failure(
                ErrorCode.COMMON_VALIDATION_FAILED.getCode(),
                ErrorCode.COMMON_VALIDATION_FAILED.getMessage(),
                fieldErrors
        );
        return ResponseEntity.status(ErrorCode.COMMON_VALIDATION_FAILED.getHttpStatus()).body(body);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Object>> handleBindException(BindException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                fieldErrors.put(err.getField(), err.getDefaultMessage())
        );

        ApiResponse<Object> body = ApiResponse.failure(
                ErrorCode.COMMON_VALIDATION_FAILED.getCode(),
                ErrorCode.COMMON_VALIDATION_FAILED.getMessage(),
                fieldErrors
        );
        return ResponseEntity.status(ErrorCode.COMMON_VALIDATION_FAILED.getHttpStatus()).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnhandledException(Exception ex, HttpServletRequest request) {
        log.error("[EXCEPTION] Unhandled error. path={}", request.getRequestURI(), ex);

        ApiResponse<Void> body = ApiResponse.failure(
                ErrorCode.COMMON_INTERNAL_ERROR.getCode(),
                ErrorCode.COMMON_INTERNAL_ERROR.getMessage()
        );
        return ResponseEntity.status(ErrorCode.COMMON_INTERNAL_ERROR.getHttpStatus()).body(body);
    }

}
