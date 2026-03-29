package com.ccksy.loan.domain.recommend.result.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RecommendationListResponse {

    private List<RecommendationListItemResponse> items;
    private int page;
    private int size;
    private int total;
}
