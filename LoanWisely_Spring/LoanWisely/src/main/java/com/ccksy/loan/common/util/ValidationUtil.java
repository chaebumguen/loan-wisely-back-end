package com.ccksy.loan.common.util;

import java.util.Collection;

/**
 * 입력 검증 보조 유틸
 *
 * 책임:
 * - null / empty / 범위 체크 보조
 * - 판단 차단 여부 결정 ❌ (Service/Engine 책임)
 */
public final class ValidationUtil {

    private ValidationUtil() {}

    public static boolean isNull(Object value) {
        return value == null;
    }

    public static boolean isNotNull(Object value) {
        return value != null;
    }

    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    public static boolean isInRange(long value, long min, long max) {
        return value >= min && value <= max;
    }
}
