package com.ccksy.loan.domain.recommend.result.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendDetailInfoResponse {

    private String description;
    private String monthlyPaymentExample;
    private String riskWarning;
}
