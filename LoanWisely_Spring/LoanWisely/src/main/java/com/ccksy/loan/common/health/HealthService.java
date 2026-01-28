package com.ccksy.loan.common.health;

/**
 * Health Check Service
 *
 * - 애플리케이션 상태 확인 책임
 * - 이후 DB, 외부 시스템 상태 체크 확장 가능
 */
public interface HealthService {

    /**
     * 애플리케이션 상태 확인
     *
     * @return 상태 문자열 (ex: OK)
     */
    String check();
}
