package com.ccksy.loan.domain.recommend.policy.scoring;

import java.util.Objects;

import com.ccksy.loan.domain.recommend.dto.internal.RecommendContext;
import com.ccksy.loan.domain.recommend.result.core.RecommendItem;

/**
 * 대출 목적 기반 점수 전략
 *
 * <p>사용자 목적과 상품 목적 태그의 일치 정도를 기준으로 점수 산정</p>
 */
public class PurposeScoreStrategy implements ScoreStrategy {

    @Override
    public double score(RecommendContext context, RecommendItem item) {
        Objects.requireNonNull(context, "RecommendContext must not be null.");
        Objects.requireNonNull(item, "RecommendItem must not be null.");

        if (context.getPurpose() == null || item.getPurposeTags() == null) {
            return 0.0;
        }

        // 목적 완전 일치 시 가중치
        if (item.getPurposeTags().contains(context.getPurpose())) {
            return 30.0;
        }

        return 0.0;
    }

    @Override
    public String getStrategyName() {
        return "PURPOSE_SCORE";
    }
}
