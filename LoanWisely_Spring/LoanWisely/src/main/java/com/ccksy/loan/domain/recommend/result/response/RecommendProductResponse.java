package com.ccksy.loan.domain.recommend.result.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RecommendProductResponse {

    private String id;
    private String lenderName;
    private String productName;
    private String rate;
    private String limit;
    private String reason;
    private Integer suitabilityScore;
    private String riskNote;
    private String providerUrl;
    private List<RecommendEstimationDetailResponse> estimationDetails;
}
