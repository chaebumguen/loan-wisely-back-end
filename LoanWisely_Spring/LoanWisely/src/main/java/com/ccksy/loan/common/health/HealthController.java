package com.ccksy.loan.common.health;

import com.ccksy.loan.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health Check Controller
 *
 * - 서버 기동 상태 확인용 API
 * - 공통 응답(ApiResponse) 구조 기준 예시
 */
@RestController
@RequiredArgsConstructor
public class HealthController {

    private final HealthService healthService;

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success(healthService.check());
    }
}
