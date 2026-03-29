package com.ccksy.loan.domain.recommend.policy.sorting;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SortStrategy {

    /**
     * @param productIds 후보 상품 ID
     * @param scores     productId -> score
     * @param rateMins   productId -> minRate (없으면 null)
     */
    List<Long> sort(List<Long> productIds,
                    Map<Long, BigDecimal> scores,
                    Map<Long, BigDecimal> rateMins);
}
