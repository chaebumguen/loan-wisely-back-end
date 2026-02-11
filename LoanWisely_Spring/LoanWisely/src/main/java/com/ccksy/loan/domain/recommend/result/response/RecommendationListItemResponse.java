package com.ccksy.loan.domain.recommend.result.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationListItemResponse {

    private String id;
    private String title;
    private String createdAt;
}
