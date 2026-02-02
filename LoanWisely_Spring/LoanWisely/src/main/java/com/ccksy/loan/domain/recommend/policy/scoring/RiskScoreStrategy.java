// FILE: domain/recommend/policy/scoring/RiskScoreStrategy.java
package com.ccksy.loan.domain.recommend.policy.scoring;

import java.util.Objects;

import com.ccksy.loan.domain.recommend.dto.internal.RecommendContext;
import com.ccksy.loan.domain.recommend.result.core.RecommendItem;

/**
 * (Strategy) 리스크 점수 (v1)
 *
 * v1 단순 모델(결정론):
 * - 사용자 위험 선호(ctx.options["riskTolerance"])와 상품 위험 지표가 가까울수록 가점
 *
 * 입력 규약(v1):
 * - ctx.options["riskTolerance"] : 1~5 (숫자/문자)
 * - item에서 risk 관련 필드는 아래 순서로 탐색:
 *   getRiskGrade, getRiskLevel, getRiskScore, getRiskBand
 *
 * 점수 산식(v1):
 * - 거리 = |user - product|
 * - score = max(0, 1.0 - 0.25 * 거리)  (같으면 1.0, 4 차이면 0)
 */
public final class RiskScoreStrategy implements ScoreStrategy {

    private static final String OPT_RISK_TOLERANCE = "riskTolerance";

    @Override
    public String id() {
        return "RiskScoreStrategy:v1";
    }

    @Override
    public double score(RecommendContext ctx, RecommendItem item) {
        Objects.requireNonNull(ctx, "ctx");
        Objects.requireNonNull(item, "item");

        Integer userTol = ScoreStrategyUtil.optInt(ctx, OPT_RISK_TOLERANCE);
        if (userTol == null) return 0.0d;

        Integer productRisk = ScoreStrategyUtil.tryGetInt(item,
                "getRiskGrade",
                "getRiskLevel",
                "getRiskScore",
                "getRiskBand");

        if (productRisk == null) return 0.0d;

        int dist = Math.abs(userTol - productRisk);
        double s = 1.0d - 0.25d * dist;
        return Math.max(0.0d, s);
    }
}
