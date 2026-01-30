package com.ccksy.loan.domain.recommend.result.decorator;

import com.ccksy.loan.domain.recommend.result.core.RecommendResult;

/**
 * 정책 안내 Decorator
 *
 * <p>추천 결과와 함께 제공할 공통 정책 안내 문구</p>
 */
public class PolicyGuideDecorator extends RecommendResultDecorator {

    public PolicyGuideDecorator(RecommendResult result) {
        super(result);
    }

    @Override
    public RecommendResult decorate() {
        // v1: 정책 안내 문구는 고정
        // 실제 메시지는 Controller Response 단계에서 병합
        return result;
    }
}
