package com.ccksy.loan.domain.recommend.result.core;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Builder(toBuilder = true)
public class RecommendResult {

    private String state;            // READY / NOT_READY / BLOCKED
    private String reproduceKey;

    private Integer resolvedInputLevel;

    private String policyVersion;
    private String metaVersion;

    private String evidenceFilePath; // DB에는 경로만 저장
    private String explainFilePath;  // DB에는 경로만 저장

    @Builder.Default
    private List<RecommendItem> items = new ArrayList<>();

    /**
     * 전역 경고/안내(상품 단위 제외사유는 다음 PART에서 productId별로 확장)
     */
    @Builder.Default
    private Map<String, String> warnings = new HashMap<>();
}
