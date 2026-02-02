package com.ccksy.loan.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 날짜/시간 공통 유틸
 *
 * 원칙:
 * - 시스템 전반에서 동일한 포맷 사용
 * - 시간 계산/판단 로직 금지
 */
public final class DateTimeUtil {

    private DateTimeUtil() {}

    public static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String format(LocalDate date) {
        return date == null ? null : date.format(DATE);
    }

    public static String format(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(DATE_TIME);
    }

    public static LocalDate parseDate(String value) {
        return value == null ? null : LocalDate.parse(value, DATE);
    }

    public static LocalDateTime parseDateTime(String value) {
        return value == null ? null : LocalDateTime.parse(value, DATE_TIME);
    }
}
