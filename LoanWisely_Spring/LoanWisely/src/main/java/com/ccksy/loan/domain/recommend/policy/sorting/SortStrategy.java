package com.ccksy.loan.domain.recommend.policy.sorting;

import java.util.Comparator;
import java.util.List;

import com.ccksy.loan.domain.recommend.result.core.RecommendItem;

/**
 * 정렬 전략 인터페이스 (Strategy Pattern)
 *
 * <p>추천 결과 아이템 리스트를 특정 기준으로 정렬한다.</p>
 */
public interface SortStrategy {

    /**
     * 정렬 Comparator 제공
     */
    Comparator<RecommendItem> getComparator();

    /**
     * 기본 정렬 수행 메서드
     */
    default void sort(List<RecommendItem> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        items.sort(getComparator());
    }

    /**
     * 전략 식별자(로그/설명용)
     */
    String getStrategyName();
}
