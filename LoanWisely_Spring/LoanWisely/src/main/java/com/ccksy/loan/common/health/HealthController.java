package com.ccksy.loan.common.health;

import com.ccksy.loan.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OP_001 : /health
 * - PUBLIC
 * - 로드밸런서 / 모니터링용 생존 확인
 *
 * ⚠ 내부 상태, 의존성 정보 노출 금지
 */
@RestController
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success(healthService.check());
    }
}
