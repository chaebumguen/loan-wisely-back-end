package com.ccksy.loan.domain.recommend.controller;

import com.ccksy.loan.common.response.ApiResponse;
import com.ccksy.loan.domain.recommend.entity.RecoEventLog;
import com.ccksy.loan.domain.recommend.entity.RecoExclusionReason;
import com.ccksy.loan.domain.recommend.entity.RecoRejectLog;
import com.ccksy.loan.domain.recommend.result.response.RecommendDetailResponse;
import com.ccksy.loan.domain.recommend.service.RecommendQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/recommendations")
public class AdminRecommendationController {

    private final RecommendQueryService recommendQueryService;

    @GetMapping("/{recommendationId}")
    public ApiResponse<RecommendDetailResponse> getDetail(@PathVariable("recommendationId") String recommendationId) {
        return ApiResponse.ok(recommendQueryService.getRecommendationDetailForAdmin(recommendationId));
    }

    @GetMapping("/event-logs")
    public ApiResponse<List<RecoEventLog>> getEventLogs(@RequestParam("productId") Long productId) {
        return ApiResponse.ok(recommendQueryService.getEventLogs(productId));
    }

    @GetMapping("/reject-logs")
    public ApiResponse<List<RecoRejectLog>> getRejectLogs(@RequestParam("requestId") Long requestId) {
        return ApiResponse.ok(recommendQueryService.getRejectLogs(requestId));
    }

    @GetMapping("/exclusion-reasons")
    public ApiResponse<List<RecoExclusionReason>> getExclusionReasons(@RequestParam("resultId") Long resultId) {
        return ApiResponse.ok(recommendQueryService.getExclusionReasons(resultId));
    }
}

