// FILE: domain/recommend/process/template/AbstractRecommendProcess.java
package com.ccksy.loan.domain.recommend.process.template;

import java.util.List;
import java.util.Objects;

import com.ccksy.loan.domain.recommend.dto.internal.RecommendContext;
import com.ccksy.loan.domain.recommend.result.core.RecommendItem;

public abstract class AbstractRecommendProcess {

    public final List<RecommendItem> execute(
            RecommendContext context,
            List<RecommendItem> candidates
    ) {
        Objects.requireNonNull(context, "RecommendContext must not be null.");
        Objects.requireNonNull(candidates, "Candidate list must not be null.");

        // 1. 사전 검증
        validate(context);

        // 2. 필터링 (통과(keep)한 후보만 반환)
        List<RecommendItem> kept = filter(context, candidates);

        // 3. 점수 산정 (통과 후보에만 적용)
        score(context, kept);

        // 4. 정렬 (통과 후보에만 적용)
        sort(kept);

        // 5. 사후 처리 (선택)
        afterProcess(context);

        return kept;
    }

    protected abstract void validate(RecommendContext context);

    protected abstract List<RecommendItem> filter(
            RecommendContext context,
            List<RecommendItem> candidates
    );

    protected abstract void score(
            RecommendContext context,
            List<RecommendItem> items
    );

    protected abstract void sort(List<RecommendItem> items);

    protected void afterProcess(RecommendContext context) {
        // no-op
    }
}
