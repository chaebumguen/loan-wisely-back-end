package com.ccksy.loan.domain.recommend.state;

import com.ccksy.loan.domain.recommend.dto.request.RecommendRequest;

public class BlockedState implements RecommendState {

    @Override
    public RecommendState handle(RecommendRequest request) {
        // 차단 상태에서는 어떤 입력에도 전이 불가
        return this;
    }

    @Override
    public String code() {
        return "BLOCKED";
    }
}
