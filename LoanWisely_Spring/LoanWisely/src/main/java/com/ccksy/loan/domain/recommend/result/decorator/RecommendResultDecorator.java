package com.ccksy.loan.domain.recommend.result.decorator;

import com.ccksy.loan.domain.recommend.result.core.RecommendResult;

/**
 * 추천 결과 Decorator 베이스
 *
 * <p>Decorator Pattern 기반으로 결과를 확장한다.</p>
 */
public abstract class RecommendResultDecorator {

    protected final RecommendResult result;

    protected RecommendResultDecorator(RecommendResult result) {
        this.result = result;
    }

    public RecommendResult getResult() {
        return result;
    }

    /**
     * 결과 확장 실행
     */
    public abstract RecommendResult decorate();
}
