// FILE: domain/recommend/command/RecommendCommandHandler.java
package com.ccksy.loan.domain.recommend.command;

import com.ccksy.loan.domain.recommend.dto.internal.RecommendContext;
import com.ccksy.loan.domain.recommend.process.template.AbstractRecommendProcess;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * (Command Handler) 추천 실행의 트랜잭션 경계 및 Fail-Fast 책임을 가진다.
 *
 * v1 규칙:
 * - DB 저장을 직접 수행하지 않는다 (mapper는 별도 패키지).
 * - 다만, process.execute() 내부에서 Evidence/Explain 저장 실패 시 예외를 던져
 *   전체 트랜잭션이 롤백되고 API 응답이 차단되도록 한다.
 */
@Component
public class RecommendCommandHandler {

    private final AbstractRecommendProcess recommendProcess;

    public RecommendCommandHandler(AbstractRecommendProcess recommendProcess) {
        this.recommendProcess = Objects.requireNonNull(recommendProcess, "recommendProcess");
    }

    /**
     * @return recommendationId (process 단계에서 생성/저장된 최종 식별자)
     */
    @Transactional(rollbackFor = Exception.class)
    public Long handle(RecommendCommand command) {
        Objects.requireNonNull(command, "command");

        // CommandLog는 "in-memory"로만 생성(저장은 process/evidence 계층에서 흡수)
        RecommendCommandLog log = RecommendCommandLog.requested(command);

        try {
            RecommendContext context = RecommendContext.from(command);
            Long recommendationId = recommendProcess.execute(context);

            // 성공(저장은 외부에서)
            log = log.success(recommendationId, LocalDateTime.now());
            return recommendationId;

        } catch (Exception e) {
            // 실패(Fail-Fast). 예외는 상위(GlobalExceptionHandler)에서 표준화.
            log = log.failed(e, LocalDateTime.now());
            throw e;
        }
    }
}
