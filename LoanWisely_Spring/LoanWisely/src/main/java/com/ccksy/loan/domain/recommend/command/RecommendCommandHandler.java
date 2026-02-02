// FILE: domain/recommend/command/RecommendCommandHandler.java
package com.ccksy.loan.domain.recommend.command;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ccksy.loan.domain.recommend.dto.internal.RecommendContext;
import com.ccksy.loan.domain.recommend.process.template.AbstractRecommendProcess;
import com.ccksy.loan.domain.recommend.result.core.RecommendItem;

/**
 * 추천 실행 Command Handler (v1, 단일 파일).
 *
 * - 트랜잭션 경계(rollbackFor=Exception) 보장
 * - Command -> RecommendContext 변환
 * - AbstractRecommendProcess.execute(context, candidates) 시그니처 정합
 * - 예외 처리(입력 오류 / 프로세스 실행 오류) 단일 파일 내에서 수행
 *
 * 주의:
 * - 여기서 "응답 포맷"을 만들지 않는다. 예외는 상위(GlobalExceptionHandler)에서 표준화.
 */
@Component
public class RecommendCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(RecommendCommandHandler.class);

    private final AbstractRecommendProcess recommendProcess;

    public RecommendCommandHandler(AbstractRecommendProcess recommendProcess) {
        this.recommendProcess = Objects.requireNonNull(recommendProcess, "recommendProcess");
    }

    /**
     * 수정안 A (후보군 외부 주입) + 단일 파일 예외 처리.
     *
     * @param command    추천 실행 명령
     * @param candidates 사전 준비된 후보군
     * @return 추천 결과 리스트
     */
    @Transactional(rollbackFor = Exception.class)
    public List<RecommendItem> handle(RecommendCommand command, List<RecommendItem> candidates) {
        try {
            // ---- 입력 검증(프리컨디션) ----
            if (command == null) {
                throw new CommandExecutionException(
                        ErrorType.INVALID_INPUT,
                        "RecommendCommand must not be null."
                );
            }
            if (candidates == null) {
                throw new CommandExecutionException(
                        ErrorType.INVALID_INPUT,
                        "Candidate list must not be null."
                );
            }

            // ---- Command -> Context ----
            // RecommendContext.from(command) 내부에서 필수 값 검증(blank 등)이 발생할 수 있음
            RecommendContext context = RecommendContext.from(command);

            // ---- Template Method 실행 ----
            return recommendProcess.execute(context, candidates);

        } catch (CommandExecutionException e) {
            // 이미 의미가 부여된 예외는 그대로 전파(트랜잭션 롤백 대상)
            log.warn("RecommendCommandHandler failed: type={}, msg={}", e.getErrorType(), e.getMessage());
            throw e;

        } catch (IllegalArgumentException e) {
            // DTO/컨텍스트 생성 과정의 파라미터 오류(예: blank version) -> 입력 오류로 정규화
            log.warn("RecommendCommandHandler invalid argument: {}", e.getMessage());
            throw new CommandExecutionException(
                    ErrorType.INVALID_INPUT,
                    e.getMessage(),
                    e
            );

        } catch (Exception e) {
            // 프로세스 실행 중 오류 -> 실행 오류로 정규화
            // (DB/외부연동/필터/스코어/정렬 등 어떤 단계든 Fail-Fast로 예외 전파)
            log.error("RecommendCommandHandler process execution error", e);
            throw new CommandExecutionException(
                    ErrorType.PROCESS_EXECUTION,
                    "Failed to execute recommendation process.",
                    e
            );
        }
    }

    /**
     * v1: 단일 파일 내 예외 타입(내부 클래스로 캡슐화).
     * - 상위(GlobalExceptionHandler)에서 errorType 기준으로 표준 응답 매핑 가능
     */
    public static final class CommandExecutionException extends RuntimeException {
        private final ErrorType errorType;

        public CommandExecutionException(ErrorType errorType, String message) {
            super(message);
            this.errorType = Objects.requireNonNull(errorType, "errorType");
        }

        public CommandExecutionException(ErrorType errorType, String message, Throwable cause) {
            super(message, cause);
            this.errorType = Objects.requireNonNull(errorType, "errorType");
        }

        public ErrorType getErrorType() {
            return errorType;
        }
    }

    public enum ErrorType {
        INVALID_INPUT,
        PROCESS_EXECUTION
    }
}
