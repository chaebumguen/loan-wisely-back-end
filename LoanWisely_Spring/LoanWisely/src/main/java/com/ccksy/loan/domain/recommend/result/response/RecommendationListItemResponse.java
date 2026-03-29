package com.ccksy.loan.domain.recommend.result.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import com.ccksy.loan.domain.recommend.result.response.RecommendationProductSummaryResponse;

@Getter
@Builder
public class RecommendationListItemResponse {

    private String id;
    private String title;
    private String createdAt;
    private List<RecommendationProductSummaryResponse> products;
}
