package com.ccksy.loan.domain.recommend.result.core;

import java.util.ArrayList;
import java.util.List;

/**
 * 추천 결과 핵심 모델
 *
 * <p>Decorator 적용 전/후의 공통 베이스가 되는 객체</p>
 */
public class RecommendResult {

    private List<RecommendItem> items = new ArrayList<>();
    private int totalCount;

    // --- getter / setter ---

    public List<RecommendItem> getItems() {
        return items;
    }

    public void setItems(List<RecommendItem> items) {
        this.items = items;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
