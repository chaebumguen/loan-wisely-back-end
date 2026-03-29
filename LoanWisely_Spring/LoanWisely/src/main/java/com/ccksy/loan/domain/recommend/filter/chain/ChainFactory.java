package com.ccksy.loan.domain.recommend.filter.chain;

import java.math.BigDecimal;
import java.util.Set;

public class ChainFactory {

    private ChainFactory() {}

    public static IneligibilityFilter createDefaultChain() {
        // v1 湲곕낯媛?異뷀썑 MANAGEMENT ?뺤콉/硫뷀?濡??대룞)
        CreditScoreFilter f1 = new CreditScoreFilter(600);
        DsrFilter f2 = new DsrFilter(new BigDecimal("0.40"));
        f1.linkWith(f2);
        return f1;
    }
}

