package com.ccksy.loan.domain.recommend.result.decorator;

import com.ccksy.loan.domain.recommend.result.core.RecommendResult;

/**
 * 설명 데이터 부착(파일 경로 기반)
 * - v1에서는 실제 파일 생성/경로 세팅을 다음 PART에서 수행
 */
public class ExplainDecorator implements RecommendResultDecorator {

    @Override
    public RecommendResult decorate(RecommendResult result) {
        return result;
    }
}
