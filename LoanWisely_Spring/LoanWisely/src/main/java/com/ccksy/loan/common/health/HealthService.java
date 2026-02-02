package com.ccksy.loan.common.health;

/**
 * Health 판단 책임 인터페이스
 *
 * 원칙:
 * - Controller는 판단하지 않는다
 * - Health 판단 기준은 Service에서 단일화
 */
public interface HealthService {

    /**
     * @return 상태 문자열 ("UP" / "DOWN")
     */
    String check();
}
