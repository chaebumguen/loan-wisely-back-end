package com.ccksy.loan.domain.recommend.process.impl;

import java.util.List;

import com.ccksy.loan.domain.recommend.dto.internal.RecommendContext;
import com.ccksy.loan.domain.recommend.process.template.AbstractRecommendProcess;
import com.ccksy.loan.domain.recommend.result.core.RecommendItem;

/**
 * ML 보조 추천 프로세스 (확장용)
 *
 * <p>v1에서는 실제 사용하지 않으며,
 * ML 연동 시 Rule 기반 결과에 가중치를 추가하거나
 * 정렬 기준을 재정의하는 용도로 확장 예정.</p>
 */
public class MlAssistedRecommendProcess extends AbstractRecommendProcess {

    @Override
    protected void validate(RecommendContext context) {
        throw new UnsupportedOperationException("ML Assisted process is not enabled in v1.");
    }

    @Override
    protected List<RecommendItem> filter(
            RecommendContext context,
            List<RecommendItem> candidates
    ) {
        throw new UnsupportedOperationException("ML Assisted process is not enabled in v1.");
    }

    @Override
    protected void score(
            RecommendContext context,
            List<RecommendItem> items
    ) {
        throw new UnsupportedOperationException("ML Assisted process is not enabled in v1.");
    }

    @Override
    protected void sort(List<RecommendItem> items) {
        throw new UnsupportedOperationException("ML Assisted process is not enabled in v1.");
    }
}
