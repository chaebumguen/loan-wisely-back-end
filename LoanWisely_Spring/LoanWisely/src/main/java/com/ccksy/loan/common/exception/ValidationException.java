package com.ccksy.loan.common.exception;

import java.util.Collections;
import java.util.Map;

/**
 * 요청값 검증 실패를 명시적으로 구분.
 * - fieldErrors는 외부에 노출될 수 있으므로 민감정보를 넣지 말 것.
 */
public class ValidationException extends BusinessException {

    private final Map<String, String> fieldErrors;

    public ValidationException(Map<String, String> fieldErrors) {
        super(ErrorCode.COMMON_VALIDATION_FAILED);
        this.fieldErrors = fieldErrors == null ? Collections.emptyMap() : Collections.unmodifiableMap(fieldErrors);
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}
