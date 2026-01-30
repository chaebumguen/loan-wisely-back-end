package com.ccksy.loan.domain.recommend.result.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.ccksy.loan.domain.recommend.result.core.RecommendItem;
import com.ccksy.loan.domain.recommend.result.core.RecommendResult;

/**
 * 추천 결과 Builder
 *
 * <p>추천 결과를 단계적으로 조립하며,
 * Decorator 적용 전의 "기본 결과"를 생성한다.</p>
 */
public class RecommendResultBuilder {

    private final List<RecommendItem> items = new ArrayList<>();

    public RecommendResultBuilder addItem(RecommendItem item) {
        Objects.requireNonNull(item, "RecommendItem must not be null.");
        this.items.add(item);
        return this;
    }

    public RecommendResultBuilder addItems(List<RecommendItem> items) {
        if (items != null) {
            this.items.addAll(items);
        }
        return this;
    }

    public RecommendResult build() {
        RecommendResult result = new RecommendResult();
        result.setItems(new ArrayList<>(items));
        result.setTotalCount(items.size());
        return result;
    }
}
