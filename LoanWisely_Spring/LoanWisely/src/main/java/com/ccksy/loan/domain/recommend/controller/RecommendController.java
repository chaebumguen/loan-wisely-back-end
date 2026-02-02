// FILE: domain/recommend/controller/RecommendController.java
package com.ccksy.loan.domain.recommend.controller;

import com.ccksy.loan.common.response.ApiResponse;
import com.ccksy.loan.domain.recommend.dto.request.RecommendRequest;
import com.ccksy.loan.domain.recommend.result.response.RecommendResponse;
import com.ccksy.loan.domain.recommend.service.RecommendFacadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * (Facade Controller)
 * - 외부에는 "추천 단일 API"만 노출
 * - 판단/정책/메타 해석 로직 금지 (ENGINE 내부로 위임)
 * - Explain/Evidence Gate는 서비스/프로세스에서 강제
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendations")
public class RecommendController {

    private final RecommendFacadeService recommendFacadeService;

    /**
     * 추천 실행
     * - v1: 요청 본문은 RecommendRequest로 수렴
     * - 사용자 식별/권한은 SecurityConfig + Service 계층에서 처리(Controller는 전달만)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RecommendResponse>> recommend(@Valid @RequestBody RecommendRequest request) {
        RecommendResponse response = recommendFacadeService.recommend(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 추천 결과 Explain 조회
     * - v1: 설명 데이터(Explain)가 없으면 "무효"로 간주되어 서비스 계층에서 차단되어야 함
     */
    @GetMapping("/{recommendationId}/explain")
    public ResponseEntity<ApiResponse<Object>> explain(@PathVariable("recommendationId") Long recommendationId) {
        Object explain = recommendFacadeService.explain(recommendationId);
        return ResponseEntity.ok(ApiResponse.success(explain));
    }
}
