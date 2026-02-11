package com.ccksy.loan.domain.recommend.result.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendExplainSummaryResponse {

    private String summary;
    private String levelUsed;
    private String levelStatus;
}
