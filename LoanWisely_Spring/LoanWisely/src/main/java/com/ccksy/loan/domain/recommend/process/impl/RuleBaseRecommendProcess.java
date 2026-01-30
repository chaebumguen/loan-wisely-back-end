package com.ccksy.loan.domain.recommend.process.impl;

import java.util.List;
import java.util.Objects;

import com.ccksy.loan.domain.recommend.dto.internal.RecommendContext;
import com.ccksy.loan.domain.recommend.filter.chain.ChainFactory;
import com.ccksy.loan.domain.recommend.filter.chain.IneligibilityFilter;
import com.ccksy.loan.domain.recommend.policy.eligibility.EligibilityPolicy;
import com.ccksy.loan.domain.recommend.policy.scoring.ScoreStrategy;
import com.ccksy.loan.domain.recommend.policy.sorting.SortStrategy;
import com.ccksy.loan.domain.recommend.process.template.AbstractRecommendProcess;
import com.ccksy.loan.domain.recommend.result.core.RecommendItem;

/**
 * 룰 기반 추천 프로세스 구현
 *
 * <p>v1 기준 기본 추천 프로세스.
 * 정책/전략은 외부에서 주입되며, 본 클래스는 흐름을 고정한다.</p>
 */
public class RuleBaseRecommendProcess extends AbstractRecommendProcess {

    private final EligibilityPolicy eligibilityPolicy;
    private final ChainFactory chainFactory;
    private final ScoreStrategy scoreStrategy;
    private final SortStrategy sortStrategy;

    public RuleBaseRecommendProcess(
            EligibilityPolicy eligibilityPolicy,
            ChainFactory chainFactory,
            ScoreStrategy scoreStrategy,
            SortStrategy sortStrategy
    ) {
        this.eligibilityPolicy = Objects.requireNonNull(eligibilityPolicy);
        this.chainFactory = Objects.requireNonNull(chainFactory);
        this.scoreStrategy = Objects.requireNonNull(scoreStrategy);
        this.sortStrategy = Objects.requireNonNull(sortStrategy);
    }

    /**
     * 1. 공통 자격 검증
     */
    @Override
    protected void validate(RecommendContext context) {
        eligibilityPolicy.preValidate(context.toFilterContext());
    }

    /**
     * 2. 부적격 필터 체인 적용
     */
    @Override
    protected List<RecommendItem> filter(
            RecommendContext context,
            List<RecommendItem> candidates
    ) {
        IneligibilityFilter chain = chainFactory.create();
        return chain.filter(context.toFilterContext(), candidates);
    }

    /**
     * 3. 점수 산정
     */
    @Override
    protected void score(
            RecommendContext context,
            List<RecommendItem> items
    ) {
        for (RecommendItem item : items) {
            double score = scoreStrategy.score(context, item);
            item.setScore(score);
        }
    }

    /**
     * 4. 정렬
     */
    @Override
    protected void sort(List<RecommendItem> items) {
        sortStrategy.sort(items);
    }

    @Override
    protected void afterProcess(RecommendContext context) {
        eligibilityPolicy.postValidate(context.toFilterContext());
    }
}
