package com.ccksy.loan.domain.user.state;

public class BeforeInputState implements UserInputState {

    @Override
    public String code() {
        return "BEFORE_INPUT";
    }

    @Override
    public String description() {
        return "필수 입력이 부족합니다.";
    }
}
