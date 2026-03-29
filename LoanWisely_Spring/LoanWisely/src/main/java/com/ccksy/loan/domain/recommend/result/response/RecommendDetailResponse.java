package com.ccksy.loan.domain.recommend.result.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RecommendDetailResponse {

    private RecommendExplainSummaryResponse explain;
    private List<RecommendProductResponse> products;
    private RecommendDetailInfoResponse detail;
}
