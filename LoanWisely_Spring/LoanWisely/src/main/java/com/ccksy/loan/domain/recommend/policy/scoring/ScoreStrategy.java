package com.ccksy.loan.domain.recommend.policy.scoring;

import com.ccksy.loan.domain.recommend.dto.internal.RecommendContext;
import com.ccksy.loan.domain.recommend.result.core.RecommendItem;

/**
 * 점수 산정 전략 인터페이스 (Strategy Pattern)
 *
 * <p>각 전략은 하나의 관점(목적, 위험도 등)에서 점수를 산정하며,
 * Composite 전략을 통해 조합될 수 있다.</p>
 */
public interface ScoreStrategy {

    /**
     * 개별 상품에 대한 점수 산정
     *
     * @param context 추천 프로세스 내부 컨텍스트
     * @param item 점수를 부여할 추천 상품
     * @return 산정된 점수 (0 이상 권장)
     */
    double score(RecommendContext context, RecommendItem item);

    /**
     * 전략 식별자(로그/설명용)
     */
    String getStrategyName();
}
