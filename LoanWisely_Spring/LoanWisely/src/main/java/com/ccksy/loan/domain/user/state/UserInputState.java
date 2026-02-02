package com.ccksy.loan.domain.user.state;

import com.ccksy.loan.domain.user.dto.request.UserProfileRequest;

public interface UserInputState {

    /**
     * 현재 상태에서 입력을 처리하고
     * 다음 상태를 반환한다.
     */
    UserInputState handle(UserProfileRequest request);

    /**
     * 상태 코드 (로그/감사/재현용)
     */
    String code();
}
