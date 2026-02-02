// FILE: domain/recommend/dto/internal/RecommendContext.java
package com.ccksy.loan.domain.recommend.dto.internal;

import com.ccksy.loan.domain.recommend.command.RecommendCommand;
import com.ccksy.loan.domain.recommend.dto.request.RecommendRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * 프로세스 내부 컨텍스트(외부 DTO와 분리).
 *
 * v1 규칙:
 * - 외부 입력(RecommendRequest/RecommendCommand)을 내부 실행 단위로 정규화
 * - 판단/계산 로직 포함 금지 (컨텍스트는 "데이터 운반"만)
 * - 결정론/재현을 위한 버전/옵션을 보관 (스냅샷/전략 ID는 v1에서 포함하지 않음)
 */
public final class RecommendContext {

    private final Long userId;

    // Approved-only references (ENGINE이 소비하는 기준)
    private final String policyVersion;
    private final String creditMetaVersion;
    private final String financialMetaVersion;

    // 결정론을 위한 옵션(TopN, flags, sourceBatchId 등) - 키 정렬은 v1에서 수행하지 않음(별도 컴포넌트 책임)
    private final Map<String, Object> options;

    // 실행 시각(감사용: 판단 결과에 영향 주면 안 됨)
    private final LocalDateTime requestedAt;

    private RecommendContext(
            Long userId,
            String policyVersion,
            String creditMetaVersion,
            String financialMetaVersion,
            Map<String, Object> options,
            LocalDateTime requestedAt
    ) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.policyVersion = requireText(policyVersion, "policyVersion");
        this.creditMetaVersion = requireText(creditMetaVersion, "creditMetaVersion");
        this.financialMetaVersion = requireText(financialMetaVersion, "financialMetaVersion");
        this.options = Collections.unmodifiableMap(options == null ? Collections.emptyMap() : options);
        this.requestedAt = Objects.requireNonNull(requestedAt, "requestedAt");
    }

    public static RecommendContext from(RecommendCommand command) {
        Objects.requireNonNull(command, "command");
        return new RecommendContext(
                command.getUserId(),
                command.getPolicyVersion(),
                command.getCreditMetaVersion(),
                command.getFinancialMetaVersion(),
                command.getOptions(),
                command.getRequestedAt()
        );
    }

    /**
     * v1 기본 매핑.
     * - userId는 인증 컨텍스트에서 주입하는 것이 원칙.
     * - request에는 userId가 없을 수 있으므로, service에서 userId를 주입하여 생성한다.
     */
    public static RecommendContext from(Long userIdFromAuth, RecommendRequest request) {
        Objects.requireNonNull(userIdFromAuth, "userIdFromAuth");
        Objects.requireNonNull(request, "request");
        return new RecommendContext(
                userIdFromAuth,
                requireText(request.getPolicyVersion(), "policyVersion"),
                requireText(request.getCreditMetaVersion(), "creditMetaVersion"),
                requireText(request.getFinancialMetaVersion(), "financialMetaVersion"),
                request.getOptions(),
                LocalDateTime.now()
        );
    }

    public Long getUserId() {
        return userId;
    }

    public String getPolicyVersion() {
        return policyVersion;
    }

    public String getCreditMetaVersion() {
        return creditMetaVersion;
    }

    public String getFinancialMetaVersion() {
        return financialMetaVersion;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.trim();
    }
}
