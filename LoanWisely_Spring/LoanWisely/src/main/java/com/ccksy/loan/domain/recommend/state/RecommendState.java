package com.ccksy.loan.domain.recommend.state;

/**
 * 추천 상태(State)
 * - v1: READY / NOT_READY / BLOCKED
 */
public interface RecommendState {

    String code();

    String description();

    /**
     * 외부 응답 차단 여부(예: 설명 누락, 필수값 누락 등)
     */
    boolean isBlocked();
}
