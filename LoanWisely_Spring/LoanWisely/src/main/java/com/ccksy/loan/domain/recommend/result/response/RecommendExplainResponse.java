package com.ccksy.loan.domain.recommend.result.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RecommendExplainResponse {

    private String summary;
    private String levelUsed;
    private String levelStatus;

    private List<String> reasons;
    private List<String> riskNotes;
}
