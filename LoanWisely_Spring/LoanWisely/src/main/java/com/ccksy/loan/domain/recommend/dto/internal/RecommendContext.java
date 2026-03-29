package com.ccksy.loan.domain.recommend.dto.internal;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 프로세스 내부 컨텍스트 (외부 DTO와 분리)
 * - domain/user, domain/consent, domain/product 로딩 결과 및 중간 산출물을 보관
 * - v1에서는 "확장 가능"하게만 최소 골격으로 둠
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RecommendContext {

    /**
     * 요청 식별
     */
    private Long userId;
    private String requestTraceId;

    /**
     * 요청 시각(프로세스 시작 시점)
     * - 동일 요청의 재현성을 위해 스냅샷 시간 기준으로 사용할 수 있음
     */
    private LocalDateTime requestedAt;

    /**
     * 최종 확정 LV (프로필/동의 로딩 후 결정)
     */
    private Integer resolvedInputLevel;

    /**
     * 동의 상태(레벨별) 요약: key=level(1~3), value=true/false
     * - consent 로딩 결과를 최소 형태로 유지
     */
    @Builder.Default
    private Map<Integer, Boolean> consentByLevel = Map.of();

    /**
     * 파생 Feature (v1: Map으로 시작, 추후 타입화 가능)
     */
    @Builder.Default
    private Map<String, Object> features = Map.of();

    /**
     * 후보 상품 ID 목록(추천 파이프라인 입력)
     */
    @Builder.Default
    private List<Long> candidateProductIds = new ArrayList<>();

    /**
     * 추천 결과 상품 ID 목록(정렬 후)
     */
    @Builder.Default
    private List<Long> recommendedProductIds = new ArrayList<>();

    /**
     * 제외 사유: key=productId, value=reasonCode(or message)
     */
    @Builder.Default
    private Map<Long, String> excludedReasons = Map.of();
}
