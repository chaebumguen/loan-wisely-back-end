package com.ccksy.loan.domain.recommend.state;

import com.ccksy.loan.domain.recommend.dto.request.RecommendRequest;

public interface RecommendState {

    /**
     * 현재 상태에서 추천 실행 가능 여부 판단.
     * 상태 전이는 구현체 내부에서만 수행한다.
     */
    RecommendState handle(RecommendRequest request);

    /**
     * 상태 식별자 (로그/감사용).
     */
    String code();
}
