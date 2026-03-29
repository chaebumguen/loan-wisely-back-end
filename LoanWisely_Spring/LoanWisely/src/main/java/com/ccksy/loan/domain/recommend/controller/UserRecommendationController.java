package com.ccksy.loan.domain.recommend.controller;

import com.ccksy.loan.common.response.ApiResponse;
import com.ccksy.loan.common.security.UserAuthUtil;
import com.ccksy.loan.domain.recommend.result.response.RecommendationListResponse;
import com.ccksy.loan.domain.recommend.service.RecommendQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me/recommendations")
@RequiredArgsConstructor
public class UserRecommendationController {

    private final RecommendQueryService recommendQueryService;

    @GetMapping
    public ApiResponse<RecommendationListResponse> list(
            Authentication authentication,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Long userId = UserAuthUtil.requireUserId(authentication);
        return ApiResponse.ok(recommendQueryService.getRecommendations(userId, page, size));
    }
}
