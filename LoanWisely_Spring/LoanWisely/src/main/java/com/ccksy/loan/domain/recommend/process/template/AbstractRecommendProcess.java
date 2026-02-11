package com.ccksy.loan.domain.recommend.process.template;

import com.ccksy.loan.domain.recommend.command.RecommendCommand;
import com.ccksy.loan.domain.recommend.result.core.RecommendResult;

/**
 * 추천 프로세스 템플릿
 * - RuleBased / ML Assisted 등 구현체로 분리
 */
public abstract class AbstractRecommendProcess {

    public final RecommendResult execute(RecommendCommand command) {
        // 공통 전/후처리가 필요하면 여기에 고정
        return doExecute(command);
    }

    protected abstract RecommendResult doExecute(RecommendCommand command);
}
