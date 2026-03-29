package com.ccksy.loan.common.util;

import java.time.*;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtil {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private DateTimeUtil() {
    }

    public static ZonedDateTime nowKst() {
        return ZonedDateTime.now(KST);
    }

    public static String formatKst(ZonedDateTime dateTime, String pattern) {
        if (dateTime == null) return null;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern).withZone(KST);
        return fmt.format(dateTime.withZoneSameInstant(KST));
    }

    public static LocalDate parseDate(String text, String pattern) {
        if (text == null || text.isBlank()) return null;
        return LocalDate.parse(text, DateTimeFormatter.ofPattern(pattern));
    }
}
