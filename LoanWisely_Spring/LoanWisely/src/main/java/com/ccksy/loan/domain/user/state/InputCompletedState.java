package com.ccksy.loan.domain.user.state;

import com.ccksy.loan.domain.user.dto.request.UserProfileRequest;

public class InputCompletedState implements UserInputState {

    @Override
    public UserInputState handle(UserProfileRequest request) {
        // 판단 불가 조건 충족 시 NotJudgableState 로 전이
        // 판단 로직 자체는 상위 계층/ENGINE 책임
        return this;
    }

    @Override
    public String code() {
        return "INPUT_COMPLETED";
    }
}
