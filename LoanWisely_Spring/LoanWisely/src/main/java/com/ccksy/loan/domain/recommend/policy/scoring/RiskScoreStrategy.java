package com.ccksy.loan.domain.recommend.policy.scoring;

import java.util.Objects;

import com.ccksy.loan.domain.recommend.dto.internal.RecommendContext;
import com.ccksy.loan.domain.recommend.result.core.RecommendItem;

/**
 * 위험도 기반 점수 전략
 *
 * <p>사용자 신용등급/위험지표 대비 상품 위험도를 비교하여 점수 산정</p>
 */
public class RiskScoreStrategy implements ScoreStrategy {

    @Override
    public double score(RecommendContext context, RecommendItem item) {
        Objects.requireNonNull(context, "RecommendContext must not be null.");
        Objects.requireNonNull(item, "RecommendItem must not be null.");

        if (context.getRiskLevel() == null || item.getRiskLevel() == null) {
            return 0.0;
        }

        int userRisk = context.getRiskLevel();
        int productRisk = item.getRiskLevel();

        // 사용자가 감당 가능한 범위 내일수록 점수 증가
        if (productRisk <= userRisk) {
            return Math.max(0, 20 - (userRisk - productRisk) * 2);
        }

        return 0.0;
    }

    @Override
    public String getStrategyName() {
        return "RISK_SCORE";
    }
}
