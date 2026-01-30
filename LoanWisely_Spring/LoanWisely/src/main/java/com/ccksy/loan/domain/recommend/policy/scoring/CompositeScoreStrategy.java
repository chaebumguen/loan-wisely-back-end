package com.ccksy.loan.domain.recommend.policy.scoring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.ccksy.loan.domain.recommend.dto.internal.RecommendContext;
import com.ccksy.loan.domain.recommend.result.core.RecommendItem;

/**
 * 점수 전략 조합기 (Composite Pattern)
 *
 * <p>여러 ScoreStrategy를 순차 실행하여 점수를 누적한다.</p>
 */
public class CompositeScoreStrategy implements ScoreStrategy {

    private final List<ScoreStrategy> strategies = new ArrayList<>();

    public CompositeScoreStrategy(List<ScoreStrategy> strategies) {
        if (strategies != null) {
            this.strategies.addAll(strategies);
        }
    }

    public List<ScoreStrategy> getStrategies() {
        return Collections.unmodifiableList(strategies);
    }

    @Override
    public double score(RecommendContext context, RecommendItem item) {
        Objects.requireNonNull(context, "RecommendContext must not be null.");
        Objects.requireNonNull(item, "RecommendItem must not be null.");

        double totalScore = 0.0;
        for (ScoreStrategy strategy : strategies) {
            totalScore += strategy.score(context, item);
        }
        return totalScore;
    }

    @Override
    public String getStrategyName() {
        return "COMPOSITE_SCORE";
    }
}
