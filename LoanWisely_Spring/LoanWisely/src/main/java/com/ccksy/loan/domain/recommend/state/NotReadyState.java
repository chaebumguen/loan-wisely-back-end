package com.ccksy.loan.domain.recommend.state;

public class NotReadyState implements RecommendState {

    @Override
    public String code() {
        return "NOT_READY";
    }

    @Override
    public String description() {
        return "필수 입력 또는 사전 조건이 부족하여 추천을 생성할 수 없습니다.";
    }

    @Override
    public boolean isBlocked() {
        return false;
    }
}
