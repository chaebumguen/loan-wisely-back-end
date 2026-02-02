package com.ccksy.loan.common.health;

import org.springframework.stereotype.Service;

/**
 * Version 1 Health 판단 구현체
 *
 * 판단 기준:
 * - 애플리케이션 프로세스 정상 동작 여부만 판단
 * - DB / 외부 API / Engine 의존성은 readiness에서 처리
 */
@Service
public class HealthServiceImpl implements HealthService {

    @Override
    public String check() {
        return "UP";
    }
}
