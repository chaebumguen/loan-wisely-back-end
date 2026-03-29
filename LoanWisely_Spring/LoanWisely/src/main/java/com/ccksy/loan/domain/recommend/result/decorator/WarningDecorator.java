package com.ccksy.loan.domain.recommend.result.decorator;

import com.ccksy.loan.domain.recommend.result.core.RecommendResult;

public class WarningDecorator implements RecommendResultDecorator {

    @Override
    public RecommendResult decorate(RecommendResult result) {
        return result;
    }
}
