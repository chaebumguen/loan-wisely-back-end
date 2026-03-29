package com.ccksy.loan.domain.recommend.state;

public class BlockedState implements RecommendState {

    @Override
    public String code() {
        return "BLOCKED";
    }

    @Override
    public String description() {
        return "정책 위반 또는 필수 근거 누락으로 인해 응답이 차단되었습니다.";
    }

    @Override
    public boolean isBlocked() {
        return true;
    }
}
