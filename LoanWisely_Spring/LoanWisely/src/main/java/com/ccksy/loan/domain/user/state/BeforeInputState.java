package com.ccksy.loan.domain.user.state;

import com.ccksy.loan.domain.user.dto.request.UserProfileRequest;

public class BeforeInputState implements UserInputState {

    @Override
    public UserInputState handle(UserProfileRequest request) {
        // 입력 수신 → 다음 상태로 전이
        return new InputCompletedState();
    }

    @Override
    public String code() {
        return "BEFORE_INPUT";
    }
}
