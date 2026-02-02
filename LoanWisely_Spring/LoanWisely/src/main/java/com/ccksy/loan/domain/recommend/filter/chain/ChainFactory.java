// FILE: domain/recommend/filter/chain/ChainFactory.java
package com.ccksy.loan.domain.recommend.filter.chain;

import com.ccksy.loan.domain.recommend.dto.internal.RecommendContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * v1 고정 산출물: ChainFactory
 *
 * 핵심 수정(v1 정합):
 * - policyVersion은 RecommendContext에 이미 존재하므로 별도 파라미터로 받지 않는다.
 * - 체인 순회 순서는 ctx.policyVersion에 의해 결정(결정론 요구).
 */
@Component
public final class ChainFactory {

    private final CreditScoreFilter creditScoreFilter;
    private final DsrFilter dsrFilter;
    private final IneligibilityFilter ineligibilityFilter;
    private final PurposeFilter purposeFilter;

    public ChainFactory(
            CreditScoreFilter creditScoreFilter,
            DsrFilter dsrFilter,
            IneligibilityFilter ineligibilityFilter,
            PurposeFilter purposeFilter
    ) {
        this.creditScoreFilter = Objects.requireNonNull(creditScoreFilter, "creditScoreFilter");
        this.dsrFilter = Objects.requireNonNull(dsrFilter, "dsrFilter");
        this.ineligibilityFilter = Objects.requireNonNull(ineligibilityFilter, "ineligibilityFilter");
        this.purposeFilter = Objects.requireNonNull(purposeFilter, "purposeFilter");
    }

    /** v1: policyVersion 기준으로 체인 ID를 고정(버전별 분기 필요 시 switch로 확장) */
    public String chainId(RecommendContext ctx) {
        Objects.requireNonNull(ctx, "ctx");
        // ctx.getPolicyVersion()을 반드시 소비(재현성 근거)
        requireText(ctx.getPolicyVersion(), "ctx.policyVersion");
        return "CHAIN_V1:CreditScore>Dsr>Ineligibility>Purpose";
    }

    /** v1: 체인 구성요소 ID 목록(스냅샷/재현키에 포함시키기 위해 상위 process에서 사용) */
    public List<String> handlerIds(RecommendContext ctx) {
        Objects.requireNonNull(ctx, "ctx");
        requireText(ctx.getPolicyVersion(), "ctx.policyVersion");
        return List.of(
                creditScoreFilter.id(),
                dsrFilter.id(),
                ineligibilityFilter.id(),
                purposeFilter.id()
        );
    }

    /**
     * Fail-Fast 체인 실행.
     *
     * @param ctx RecommendContext(승인본 버전/옵션 포함)
     * @param candidate 상품 후보(Map; v1에서 후보 모델 클래스 추가 생성 금지)
     * @return pass면 null, fail이면 reasonCode 반환
     */
    public String evaluate(RecommendContext ctx, Map<String, Object> candidate) {
        Objects.requireNonNull(ctx, "ctx");
        Objects.requireNonNull(candidate, "candidate");

        // v1 결정론 근거: policyVersion은 ctx에서만 읽는다.
        requireText(ctx.getPolicyVersion(), "ctx.policyVersion");

        String reason;

        reason = creditScoreFilter.apply(ctx, candidate);
        if (reason != null) return reason;

        reason = dsrFilter.apply(ctx, candidate);
        if (reason != null) return reason;

        reason = ineligibilityFilter.apply(ctx, candidate);
        if (reason != null) return reason;

        reason = purposeFilter.apply(ctx, candidate);
        if (reason != null) return reason;

        return null; // PASS
    }

    private static String requireText(String v, String field) {
        if (v == null || v.trim().isEmpty()) throw new IllegalArgumentException(field + " must not be blank");
        return v.trim();
    }
}
