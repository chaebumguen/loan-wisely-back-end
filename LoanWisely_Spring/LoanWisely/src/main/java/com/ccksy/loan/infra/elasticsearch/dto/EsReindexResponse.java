package com.ccksy.loan.infra.elasticsearch.dto;

public class EsReindexResponse {

    private final int recoPolicyCount;
    private final int recommendHistoryCount;

    public EsReindexResponse(int recoPolicyCount, int recommendHistoryCount) {
        this.recoPolicyCount = recoPolicyCount;
        this.recommendHistoryCount = recommendHistoryCount;
    }

    public int getRecoPolicyCount() {
        return recoPolicyCount;
    }

    public int getRecommendHistoryCount() {
        return recommendHistoryCount;
    }
}
