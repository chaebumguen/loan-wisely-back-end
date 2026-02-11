package com.ccksy.loan.domain.recommend.process.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ccksy.loan.domain.recommend.command.RecommendCommand;
import com.ccksy.loan.domain.recommend.process.template.AbstractRecommendProcess;
import com.ccksy.loan.domain.recommend.result.builder.RecommendResultBuilder;
import com.ccksy.loan.domain.recommend.result.core.RecommendResult;
import com.ccksy.loan.domain.recommend.state.NotReadyState;

/**
 * 향후 확장용 ML 보조
 * - v1에서는 구현하지 않고 NOT_READY로만 반환(안전 책임 분리)
 */
@Component
@Profile("ml")
public class MlAssistedRecommendProcess extends AbstractRecommendProcess {

    @Override
    protected RecommendResult doExecute(RecommendCommand command) {
    	var b = RecommendResultBuilder.notReady(command.getReproduceKey(), new NotReadyState().code())
    	        .resolvedInputLevel(command.getRequestedInputLevel());

        b = RecommendResultBuilder.addGlobalWarning(b, "ML_NOT_ENABLED", "v1에서는 ML 보조 프로세스가 비활성화되어 있습니다.");

    	return b.build();
    }
}
