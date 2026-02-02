// FILE: domain/recommend/command/RecommendCommand.java
package com.ccksy.loan.domain.recommend.command;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * (Command) 추천 실행 요청을 감사 가능한 "명령" 단위로 캡슐화한다.
 *
 * v1 규칙:
 * - 판단/추천 로직 포함 금지
 * - DB 접근 금지 (mapper는 domain/recommend/mapper로 귀속)
 * - 결정론 키 생성에 필요한 식별자(버전/옵션)는 반드시 포함
 */
public final class RecommendCommand {

    private final Long userId;

    /** Approved/Active policy version id or version string */
    private final String policyVersion;

    /** Approved credit metadata version id or version string */
    private final String creditMetaVersion;

    /** Approved financial metadata version id or version string */
    private final String financialMetaVersion;

    /**
     * 결정론/재현을 위해 옵션을 포함한다.
     * - 예: topN, includeReasons, includeWarnings, sourceBatchId 등
     */
    private final Map<String, Object> options;

    /** 감사용. 판단 결과에 영향 주면 안 됨 */
    private final LocalDateTime requestedAt;

    public RecommendCommand(
            Long userId,
            String policyVersion,
            String creditMetaVersion,
            String financialMetaVersion,
            Map<String, Object> options
    ) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.policyVersion = requireText(policyVersion, "policyVersion");
        this.creditMetaVersion = requireText(creditMetaVersion, "creditMetaVersion");
        this.financialMetaVersion = requireText(financialMetaVersion, "financialMetaVersion");
        this.options = Collections.unmodifiableMap(options == null ? Collections.emptyMap() : options);
        this.requestedAt = LocalDateTime.now();
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
