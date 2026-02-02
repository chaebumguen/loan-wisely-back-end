// FILE: domain/recommend/policy/scoring/ScoreStrategy.java
package com.ccksy.loan.domain.recommend.policy.scoring;

import java.util.List;
import java.util.Objects;

import com.ccksy.loan.domain.recommend.dto.internal.RecommendContext;
import com.ccksy.loan.domain.recommend.result.core.RecommendItem;

/**
 * (Strategy) 점수 전략 인터페이스 (v1)
 *
 * v1 원칙:
 * - 결정론(REQ-067): 랜덤/시간 의존 금지
 * - Strategy는 "점수 계산"만 담당(필터/정렬/Explain 저장 금지)
 *
 * 통합 방식:
 * - 각 구현체는 item 하나에 대해 score를 산출한다.
 * - 반환 점수는 정렬/타이브레이크에 사용될 수 있다.
 */
public interface ScoreStrategy {

    /**
     * 전략 ID(재현키/스냅샷에 기록 가능한 문자열)
     */
    String id();

    /**
     * item 단건 점수 산출
     */
    double score(RecommendContext ctx, RecommendItem item);

    /**
     * item 리스트에 대해 점수 계산 후, 가능하면 RecommendItem에 반영한다.
     *
     * NOTE:
     * - RecommendItem의 score 필드/세터 존재 여부는 프로젝트 구현에 따라 다를 수 있으므로
     *   v1에서는 reflection으로 "setScore(double|Double|BigDecimal)"만 시도한다.
     * - 세터가 없으면 "계산만" 수행하고 조용히 종료한다(컴파일 안정).
     */
    default void apply(RecommendContext ctx, List<RecommendItem> items) {
        Objects.requireNonNull(ctx, "ctx");
        Objects.requireNonNull(items, "items");

        for (RecommendItem item : items) {
            if (item == null) continue;
            double s = score(ctx, item);
            ScoreStrategyUtil.trySetScore(item, s);
        }
    }
}
