package com.ccksy.loan.domain.user.state;

public class InputCompletedState implements UserInputState {

    @Override
    public String code() {
        return "INPUT_COMPLETED";
    }

    @Override
    public String description() {
        return "필수 입력이 완료되었습니다.";
    }
}
