package com.ccksy.loan.domain.recommend.result.response;

import com.ccksy.loan.domain.recommend.result.core.RecommendResult;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class RecommendResponse {

    private String recommendationId;
    private String state;
    private String reproduceKey;
    private Integer inputLevel;

    private List<RecommendItemResponse> items;

    public static RecommendResponse from(RecommendResult result, Long recommendId) {
        return RecommendResponse.builder()
                .recommendationId(recommendId == null ? null : String.valueOf(recommendId))
                .state(result.getState())
                .reproduceKey(result.getReproduceKey())
                .inputLevel(result.getResolvedInputLevel())
                .items(result.getItems().stream().map(RecommendItemResponse::from).collect(Collectors.toList()))
                .build();
    }
}
