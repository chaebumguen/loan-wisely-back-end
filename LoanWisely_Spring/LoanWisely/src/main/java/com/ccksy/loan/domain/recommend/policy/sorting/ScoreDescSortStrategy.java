package com.ccksy.loan.domain.recommend.policy.sorting;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScoreDescSortStrategy implements SortStrategy {

    @Override
    public List<Long> sort(List<Long> productIds, Map<Long, BigDecimal> scores, Map<Long, BigDecimal> rateMins) {
        return productIds.stream()
                .sorted(Comparator
                        .comparing((Long id) -> scores.getOrDefault(id, BigDecimal.ZERO))
                        .reversed()
                        .thenComparing(id -> id)) // 타이브레이크(일단 productId)
                .collect(Collectors.toList());
    }
}
