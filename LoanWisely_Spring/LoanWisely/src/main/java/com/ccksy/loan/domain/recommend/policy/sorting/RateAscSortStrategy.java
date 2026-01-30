package com.ccksy.loan.domain.recommend.policy.sorting;

import java.util.Comparator;

import com.ccksy.loan.domain.recommend.result.core.RecommendItem;

/**
 * 금리 오름차순 정렬 전략
 */
public class RateAscSortStrategy implements SortStrategy {

    @Override
    public Comparator<RecommendItem> getComparator() {
        return Comparator
                .comparingDouble(RecommendItem::getInterestRate);
    }

    @Override
    public String getStrategyName() {
        return "RATE_ASC";
    }
}
