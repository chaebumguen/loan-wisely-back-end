package com.ccksy.loan.common.exception;

import com.ccksy.loan.common.response.ApiResponse;

import jakarta.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 핸들러
 *
 * - Controller에서는 예외를 직접 처리하지 않는다.
 * - 모든 예외는 여기서 ApiResponse 형태로 변환된다.
 * - 에러 응답 포맷을 강제하여 API 응답 일관성을 유지한다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* =========================
     * Business Exception
     * ========================= */

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException ex) {

        log.warn("[BUSINESS_EXCEPTION] code={}, message={}",
                ex.getErrorCode().getCode(),
                ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getErrorCode(), ex.getMessage()));
    }

    /* =========================
     * Validation Exception (Custom)
     * ========================= */

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            ValidationException ex) {

        log.warn("[VALIDATION_EXCEPTION] code={}, message={}",
                ex.getErrorCode().getCode(),
                ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getErrorCode(), ex.getMessage()));
    }

    /* =========================
     * @Valid DTO 검증 실패
     * ========================= */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .orElse("입력값 검증에 실패했습니다.");

        log.warn("[METHOD_ARGUMENT_NOT_VALID] message={}", message);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCode.VALIDATION_FAILED, message));
    }

    /* =========================
     * @RequestParam / @PathVariable 검증 실패
     * ========================= */

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(
            ConstraintViolationException ex) {

        String message = ex.getConstraintViolations()
                .stream()
                .findFirst()
                .map(v -> v.getPropertyPath() + " : " + v.getMessage())
                .orElse("요청 파라미터 검증에 실패했습니다.");

        log.warn("[CONSTRAINT_VIOLATION] message={}", message);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCode.VALIDATION_FAILED, message));
    }

    /* =========================
     * 예상하지 못한 모든 예외
     * ========================= */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {

        // 내부 구현 노출 방지
        log.error("[UNEXPECTED_EXCEPTION]", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorCode.SYSTEM_ERROR));
    }
}