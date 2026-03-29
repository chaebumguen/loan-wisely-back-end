package com.ccksy.loan.common.exception;

import java.util.Collections;
import java.util.Map;

public class ValidationException extends BusinessException {

    private final Map<String, String> fieldErrors;

    public ValidationException(Map<String, String> fieldErrors) {
        super(ErrorCode.VALIDATION_FAILED);
        this.fieldErrors = fieldErrors == null ? Collections.emptyMap() : Collections.unmodifiableMap(fieldErrors);
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}
