package com.ccksy.loan.domain.recommend.process.impl;

import com.ccksy.loan.domain.recommend.command.RecommendCommand;
import com.ccksy.loan.domain.recommend.filter.chain.ChainFactory;
import com.ccksy.loan.domain.recommend.filter.chain.IneligibilityFilter;
import com.ccksy.loan.domain.recommend.filter.model.ExclusionReason;
import com.ccksy.loan.domain.recommend.filter.model.FilterContext;
import com.ccksy.loan.domain.recommend.policy.eligibility.DefaultEligibilityPolicy;
import com.ccksy.loan.domain.recommend.policy.eligibility.EligibilityPolicy;
import com.ccksy.loan.domain.recommend.process.template.AbstractRecommendProcess;
import com.ccksy.loan.domain.recommend.result.builder.RecommendResultBuilder;
import com.ccksy.loan.domain.recommend.result.core.RecommendResult;
import com.ccksy.loan.domain.recommend.service.RiskScoringService;
import com.ccksy.loan.domain.recommend.service.model.RiskScoreResult;
import com.ccksy.loan.domain.recommend.state.NotReadyState;
import com.ccksy.loan.domain.recommend.state.ReadyState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * v1: 룰 기반 추천 프로세스(공격)
 * - 실제 로딩(UserProfile/UserConsent/Product 조회/로딩/정렬)은 다음 PART에서 연결
 */
@Component
@Profile("!ml")
@RequiredArgsConstructor
@Slf4j
public class RuleBasedRecommendProcess extends AbstractRecommendProcess {

    private final EligibilityPolicy eligibilityPolicy = new DefaultEligibilityPolicy();
    private final RiskScoringService riskScoringService;

    @Override
    protected RecommendResult doExecute(RecommendCommand command) {

        // v1 최소 FilterContext(실제 값은 다음 PART에서 로딩/feature로 채움)
        RiskScoreResult risk = riskScoringService.score(command.getUserId(), command.getRequestedInputLevel());

        FilterContext filterContext = new FilterContext(
                command.getUserId(),
                command.getRequestedInputLevel(),
                risk.getCreditScore(),
                risk.getDsr(),
                risk.getLoanPurposeCode(),
                LocalDateTime.now()
        );

        if (!eligibilityPolicy.isEligible(filterContext)) {
            return RecommendResultBuilder.notReady(command.getReproduceKey(), new NotReadyState().code())
                    .resolvedInputLevel(command.getRequestedInputLevel())
                    .build();
        }

        // 필터 체인(현재 기본 체인: creditScore/dsr가 null이면 제외 사유 발생)
        IneligibilityFilter chain = ChainFactory.createDefaultChain();
        Optional<ExclusionReason> reason = chain.check(filterContext);

        if (reason.isPresent()) {
        	var b = RecommendResultBuilder.notReady(command.getReproduceKey(), new NotReadyState().code())
        	        .resolvedInputLevel(command.getRequestedInputLevel());

        	b = RecommendResultBuilder.addGlobalWarning(b, reason.get().getCode(), reason.get().getMessage());

        	return b.build();
        }

        // TODO: 상품 로딩 + 조건 로직 + 정렬 + 제외사유(productId별)
        return RecommendResultBuilder.ready(command.getReproduceKey(), new ReadyState().code())
                .resolvedInputLevel(command.getRequestedInputLevel())
                .build();
    }
}
