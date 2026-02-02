// FILE: domain/recommend/policy/scoring/PurposeScoreStrategy.java
package com.ccksy.loan.domain.recommend.policy.scoring;

import java.util.Objects;

import com.ccksy.loan.domain.recommend.dto.internal.RecommendContext;
import com.ccksy.loan.domain.recommend.result.core.RecommendItem;

/**
 * (Strategy) 대출 목적 적합도 점수 (v1)
 *
 * - ctx.options["loanPurpose"] 와 상품의 목적 코드/문자열이 일치하면 가점
 * - 목적 정보가 없으면 0점(결측은 불리/유리로 치우치지 않도록 중립 처리)
 */
public final class PurposeScoreStrategy implements ScoreStrategy {

    // v1 고정 키(RecommendContext.options)
    private static final String OPT_LOAN_PURPOSE = "loanPurpose";

    @Override
    public String id() {
        return "PurposeScoreStrategy:v1";
    }

    @Override
    public double score(RecommendContext ctx, RecommendItem item) {
        Objects.requireNonNull(ctx, "ctx");
        Objects.requireNonNull(item, "item");

        String userPurpose = ScoreStrategyUtil.optText(ctx, OPT_LOAN_PURPOSE);
        if (userPurpose == null) return 0.0d;

        // 상품 목적 추출: getLoanPurpose / getPurpose / getPurposeCode / getLoanPurposeCode 순서로 시도
        String itemPurpose =
                ScoreStrategyUtil.tryGetText(item, "getLoanPurpose",
                        "getPurpose",
                        "getPurposeCode",
                        "getLoanPurposeCode");

        if (itemPurpose == null) return 0.0d;

        return ScoreStrategyUtil.equalsIgnoreSpace(userPurpose, itemPurpose) ? 1.0d : 0.0d;
    }
}
