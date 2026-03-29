package com.ccksy.loan.domain.recommend.command;

import com.ccksy.loan.domain.recommend.process.template.AbstractRecommendProcess;
import com.ccksy.loan.domain.recommend.result.core.RecommendResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Command → Process 실행 진입점
 */
@Component
@RequiredArgsConstructor
public class RecommendCommandHandler {

    private final AbstractRecommendProcess recommendProcess;

    public RecommendResult handle(RecommendCommand command) {
        return recommendProcess.execute(command);
    }
}
