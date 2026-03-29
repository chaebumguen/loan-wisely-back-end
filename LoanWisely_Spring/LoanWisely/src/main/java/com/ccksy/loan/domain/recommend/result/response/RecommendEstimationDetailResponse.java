package com.ccksy.loan.domain.recommend.result.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendEstimationDetailResponse {

    private String factorCode;
    private String factorName;
    private String factorValue;
    private String contribution;
}
