package com.ccksy.loan.domain.recommend.policy.sorting;

import java.util.Comparator;

import com.ccksy.loan.domain.recommend.result.core.RecommendItem;

/**
 * 점수 내림차순 정렬 전략
 */
public class ScoreDescSortStrategy implements SortStrategy {

    @Override
    public Comparator<RecommendItem> getComparator() {
        return Comparator
                .comparingDouble(RecommendItem::getScore)
                .reversed();
    }

    @Override
    public String getStrategyName() {
        return "SCORE_DESC";
    }
}
