package com.ccksy.loan.domain.recommend.result.decorator;

import com.ccksy.loan.domain.recommend.result.core.RecommendResult;

public class PolicyGuideDecorator implements RecommendResultDecorator {

    @Override
    public RecommendResult decorate(RecommendResult result) {
        return result;
    }
}
