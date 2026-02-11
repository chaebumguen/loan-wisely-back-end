package com.ccksy.loan.domain.recommend.controller;

import com.ccksy.loan.common.response.ApiResponse;
import com.ccksy.loan.domain.recommend.result.response.RecommendDetailResponse;
import com.ccksy.loan.domain.recommend.service.RecommendQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/recommendations")
public class AdminRecommendationController {

    private final RecommendQueryService recommendQueryService;

    @GetMapping("/{recommendationId}")
    public ApiResponse<RecommendDetailResponse> getDetail(@PathVariable String recommendationId) {
        return ApiResponse.ok(recommendQueryService.getRecommendationDetailForAdmin(recommendationId));
    }
}
