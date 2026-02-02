package com.ccksy.loan.domain.recommend.state;

import com.ccksy.loan.domain.recommend.dto.request.RecommendRequest;

public class ReadyState implements RecommendState {

    @Override
    public RecommendState handle(RecommendRequest request) {
        // 이미 추천 가능 상태 → 상태 유지
        return this;
    }

    @Override
    public String code() {
        return "READY";
    }
}
