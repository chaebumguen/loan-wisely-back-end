package com.ccksy.loan.domain.user.state;

import com.ccksy.loan.domain.user.dto.request.UserProfileRequest;

public class NotJudgableState implements UserInputState {

    @Override
    public UserInputState handle(UserProfileRequest request) {
        // 판단 불가 상태 고정
        return this;
    }

    @Override
    public String code() {
        return "NOT_JUDGABLE";
    }
}
