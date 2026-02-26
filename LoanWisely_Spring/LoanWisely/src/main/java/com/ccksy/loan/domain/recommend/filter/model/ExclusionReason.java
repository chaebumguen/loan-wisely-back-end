package com.ccksy.loan.domain.recommend.filter.model;

public class ExclusionReason {

    private final String code;
    private final String message;
    private final String detail;

    public ExclusionReason(String code, String message) {
        this(code, message, null);
    }

    public ExclusionReason(String code, String message, String detail) {
        this.code = code;
        this.message = message;
        this.detail = detail;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public String getDetail() { return detail; }

    public static ExclusionReason of(String code, String message) {
        return new ExclusionReason(code, message);
    }

    public static ExclusionReason of(String code, String message, String detail) {
        return new ExclusionReason(code, message, detail);
    }
}
