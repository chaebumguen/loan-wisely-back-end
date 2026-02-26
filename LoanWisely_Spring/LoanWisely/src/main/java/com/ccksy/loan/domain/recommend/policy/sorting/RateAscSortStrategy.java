package com.ccksy.loan.domain.recommend.policy.sorting;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RateAscSortStrategy implements SortStrategy {

    @Override
    public List<Long> sort(List<Long> productIds, Map<Long, BigDecimal> scores, Map<Long, BigDecimal> rateMins) {
        return productIds.stream()
                .sorted(Comparator
                        .comparing((Long id) -> rateMins.getOrDefault(id, new BigDecimal("999")))
                        .thenComparing(id -> scores.getOrDefault(id, BigDecimal.ZERO).negate()) // 금리 동일 시 점수 높은 것 우선
                        .thenComparing(id -> id))
                .collect(Collectors.toList());
    }
}
