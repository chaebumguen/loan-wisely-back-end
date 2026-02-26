package com.ccksy.loan.common.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public final class MoneyUtil {

    private MoneyUtil() {
    }

    public static BigDecimal parse(String amount) {
        if (amount == null || amount.isBlank()) return null;
        String normalized = amount.replaceAll("[,\\s]", "");
        return new BigDecimal(normalized);
    }

    public static String format(BigDecimal amount) {
        if (amount == null) return null;
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.KOREA);
        nf.setGroupingUsed(true);
        nf.setMaximumFractionDigits(0);
        return nf.format(amount);
    }
}
