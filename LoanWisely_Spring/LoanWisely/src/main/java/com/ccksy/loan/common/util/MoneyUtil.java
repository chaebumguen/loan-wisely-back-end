package com.ccksy.loan.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 금액 처리 공통 유틸
 *
 * ⚠ 주의:
 * - 금리/한도/점수 계산 금지 (ENGINE 책임)
 * - 반올림 규칙 통일 목적
 */
public final class MoneyUtil {

    private MoneyUtil() {}

    /** 원 단위 절사 */
    public static BigDecimal floorWon(BigDecimal value) {
        if (value == null) return null;
        return value.setScale(0, RoundingMode.FLOOR);
    }

    /** 소수점 n자리 반올림 */
    public static BigDecimal round(BigDecimal value, int scale) {
        if (value == null) return null;
        return value.setScale(scale, RoundingMode.HALF_UP);
    }

    /** null-safe BigDecimal */
    public static BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
