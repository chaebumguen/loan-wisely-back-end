package com.ccksy.loan.domain.recommend.result.decorator;

import com.ccksy.loan.domain.recommend.result.core.RecommendResult;

public interface RecommendResultDecorator {
    RecommendResult decorate(RecommendResult result);
}
