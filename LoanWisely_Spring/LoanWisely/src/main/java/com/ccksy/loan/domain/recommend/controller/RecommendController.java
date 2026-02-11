package com.ccksy.loan.domain.recommend.controller;

import com.ccksy.loan.common.response.ApiResponse;
import com.ccksy.loan.common.security.UserAuthUtil;
import com.ccksy.loan.domain.recommend.dto.request.RecommendRequest;
import com.ccksy.loan.domain.recommend.result.response.RecommendDetailResponse;
import com.ccksy.loan.domain.recommend.result.response.RecommendExplainResponse;
import com.ccksy.loan.domain.recommend.result.response.RecommendResponse;
import com.ccksy.loan.domain.recommend.service.RecommendFacadeService;
import com.ccksy.loan.domain.recommend.service.RecommendQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendFacadeService recommendFacadeService;
    private final RecommendQueryService recommendQueryService;

    /**
     * (설계서 기준) 추천 실행
     */
    @PostMapping
    public ApiResponse<RecommendResponse> recommend(
            Authentication authentication,
            @Valid @RequestBody RecommendRequest request
    ) {
        Long userId = UserAuthUtil.requireUserId(authentication);
        request.setUserId(userId);
        return ApiResponse.ok(recommendFacadeService.recommend(request));
    }

    /**
     * (임시) 재현 키 기반 재실행
     * - 설계서에는 직접 명시되지 않으므로 추후 변경/폐기 가능
     */
    @GetMapping("/reproduce/{reproduceKey}")
    public ApiResponse<RecommendResponse> reproduce(@PathVariable String reproduceKey) {
        return ApiResponse.ok(recommendFacadeService.reproduce(reproduceKey));
    }

    @GetMapping("/{recommendationId}")
    public ApiResponse<RecommendDetailResponse> getDetail(
            Authentication authentication,
            @PathVariable String recommendationId
    ) {
        Long userId = UserAuthUtil.requireUserId(authentication);
        return ApiResponse.ok(recommendQueryService.getRecommendationDetail(userId, recommendationId));
    }

    @GetMapping("/{recommendationId}/explain")
    public ApiResponse<RecommendExplainResponse> getExplain(
            Authentication authentication,
            @PathVariable String recommendationId
    ) {
        Long userId = UserAuthUtil.requireUserId(authentication);
        return ApiResponse.ok(recommendQueryService.getRecommendationExplain(userId, recommendationId));
    }
}
