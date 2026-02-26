package com.ccksy.loan.domain.recommend.result.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationProductSummaryResponse {
    private String productName;
    private String rate;
    private String limit;
    private String repaymentMethod;
}

