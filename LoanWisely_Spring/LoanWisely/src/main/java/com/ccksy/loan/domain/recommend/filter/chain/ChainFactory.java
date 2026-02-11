package com.ccksy.loan.domain.recommend.filter.chain;

import java.math.BigDecimal;
import java.util.Set;

public class ChainFactory {

    private ChainFactory() {}

    public static IneligibilityFilter createDefaultChain() {
        // v1 기본값(추후 MANAGEMENT 정책/메타로 이동)
        CreditScoreFilter f1 = new CreditScoreFilter(600);
        DsrFilter f2 = new DsrFilter(new BigDecimal("0.40"));
        PurposeFilter f3 = new PurposeFilter(Set.of("LIVING", "EDU", "BUSINESS", "ETC"));

        f1.linkWith(f2).linkWith(f3);
        return f1;
    }
}
