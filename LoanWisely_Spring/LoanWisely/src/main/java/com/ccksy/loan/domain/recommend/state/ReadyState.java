package com.ccksy.loan.domain.recommend.state;

public class ReadyState implements RecommendState {

    @Override
    public String code() {
        return "READY";
    }

    @Override
    public String description() {
        return "추천 결과 생성이 완료되었습니다.";
    }

    @Override
    public boolean isBlocked() {
        return false;
    }
}
