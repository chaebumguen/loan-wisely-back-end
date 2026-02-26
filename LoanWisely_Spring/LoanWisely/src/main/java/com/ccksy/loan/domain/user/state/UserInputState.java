package com.ccksy.loan.domain.user.state;

/**
 * 사용자 입력 상태(State)
 * - "입력전/입력완료/판단불가" 정도의 최소 상태만 우선 정의
 */
public interface UserInputState {
    String code();
    String description();
}
