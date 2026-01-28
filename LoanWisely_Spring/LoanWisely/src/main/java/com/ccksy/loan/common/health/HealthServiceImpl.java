package com.ccksy.loan.common.health;

import org.springframework.stereotype.Service;

/**
 * Health Check Service 구현체
 */
@Service
public class HealthServiceImpl implements HealthService {

    @Override
    public String check() {
        // 현재는 단순 서버 기동 여부만 확인
        // 추후 DB, Redis, 외부 API 상태 체크 가능
        return "OK";
    }
}
