package com.ccksy.loan.common.exception;

import com.ccksy.loan.common.response.ApiResponse;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        ErrorCode code = ex.getErrorCode() != null ? ex.getErrorCode() : ErrorCode.INTERNAL_ERROR;
        log.warn("BusinessException: code={}, message={}", code.getCode(), ex.getMessage());
        return ResponseEntity.status(code.getHttpStatus())
                .body(ApiResponse.fail(code.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(ValidationException ex) {
        ErrorCode code = ex.getErrorCode() != null ? ex.getErrorCode() : ErrorCode.VALIDATION_FAILED;
        log.warn("ValidationException: code={}, fields={}", code.getCode(), ex.getFieldErrors());
        return ResponseEntity.status(code.getHttpStatus())
                .body(ApiResponse.fail(code.getCode(), code.getMessage(), ex.getFieldErrors()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleBeanValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        ValidationException wrapped = new ValidationException(errors);
        return handleValidation(wrapped);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnknown(Exception ex) {
        ErrorCode code = ErrorCode.INTERNAL_ERROR;
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(code.getHttpStatus())
                .body(ApiResponse.fail(code.getCode(), code.getMessage()));
    }
}
