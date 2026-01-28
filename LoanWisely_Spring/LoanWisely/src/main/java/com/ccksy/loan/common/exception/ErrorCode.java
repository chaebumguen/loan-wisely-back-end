package com.ccksy.loan.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 시스템 전반에서 사용하는 표준 에러 코드 정의
 *
 * 코드 규칙:
 * - Cxxx : Client Error (요청/입력 오류)
 * - Bxxx : Business Rule Error (비즈니스 규칙 위반)
 * - Sxxx : System Error (서버/외부 시스템 오류)
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	
	/*
	 * enum이란 : 
	 *  - enum은 “미리 정해진 값만 가질 수 있는 타입”
	 *  - 문자열(String) 여러 개를 그냥 쓰는 게 아니라 “선택지 목록 자체를 하나의 타입으로 만든 것”이라고 생각하면 됨
	 */

    /* =========================
     * Client / Validation Errors (C)
     * ========================= */

    INVALID_REQUEST("C001", "잘못된 요청입니다."),
    VALIDATION_FAILED("C002", "입력값 검증에 실패했습니다."),
    MISSING_REQUIRED_FIELD("C003", "필수 입력값이 누락되었습니다."),
    INVALID_FORMAT("C004", "입력값 형식이 올바르지 않습니다."),

    /* =========================
     * Business Rule Errors (B)
     * ========================= */

    BUSINESS_RULE_VIOLATION("B001", "비즈니스 규칙을 위반했습니다."),
    NOT_ELIGIBLE("B002", "현재 조건에서는 추천이 불가능합니다."),
    INSUFFICIENT_CREDIT_SCORE("B003", "신용 점수가 기준에 미달합니다."),
    INSUFFICIENT_PAYMENT_ABILITY("B004", "상환 능력이 부족합니다."),
    PRODUCT_NOT_AVAILABLE("B005", "현재 이용 가능한 상품이 없습니다."),

    /* =========================
     * System / Server Errors (S)
     * ========================= */

    SYSTEM_ERROR("S001", "시스템 오류가 발생했습니다."),
    DATABASE_ERROR("S002", "데이터베이스 처리 중 오류가 발생했습니다."),
    EXTERNAL_API_ERROR("S003", "외부 시스템 연동 중 오류가 발생했습니다."),
    ML_SERVER_ERROR("S004", "추천 분석 시스템 오류가 발생했습니다.");

    private final String code;
    private final String message;
}
