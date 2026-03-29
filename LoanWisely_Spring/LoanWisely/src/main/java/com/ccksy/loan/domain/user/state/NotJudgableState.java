package com.ccksy.loan.domain.user.state;

public class NotJudgableState implements UserInputState {

    @Override
    public String code() {
        return "NOT_JUDGABLE";
    }

    @Override
    public String description() {
        return "입력값이 유효하지 않아 판단이 불가능합니다.";
    }
}
