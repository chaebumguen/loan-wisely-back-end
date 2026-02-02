package com.ccksy.loan.adapter.ml;

import java.time.Instant;
import java.util.*;

/**
 * MlRequestAdapter
 * - 내부 컨텍스트/피처 스냅샷을 "외부 ML 서버 요청" 형태로 변환하는 어댑터입니다.
 * - (Version 1) 외부 ML API 스펙이 확정되지 않았으므로, Map 기반의 확장 가능한 요청 구조로 제공합니다.
 *
 * 결정론/재현성:
 * - 호출자가 넘겨준 snapshot/version을 그대로 포함하여 재현 키를 만들 수 있게 설계합니다.
 * - Adapter 내부에서 임의 랜덤/비결정 값을 생성하지 않습니다.
 */
public class MlRequestAdapter {

    /**
     * 외부 ML 서버로 전달할 범용 요청 DTO
     * - payload: 외부에서 요구하는 입력 구조가 확정되기 전까지 Map 기반 유지
     */
    public static final class MlScoreRequest {
        private final String modelKey;                 // 예: "risk-score-v1" 등 (없으면 null 허용)
        private final String userId;                   // 내부 사용자 식별자(문자열화)
        private final String featureSnapshotId;        // 피처 스냅샷 ID
        private final String creditDictionaryVersionId;// 메타/사전 버전 ID
        private final String policyVersion;            // 정책 버전(있다면)
        private final Map<String, Object> payload;     // 피처/입력/옵션
        private final Instant requestedAt;             // 요청 시각(결정론 목적: 호출자가 제공하는 것을 권장)

        private MlScoreRequest(
                String modelKey,
                String userId,
                String featureSnapshotId,
                String creditDictionaryVersionId,
                String policyVersion,
                Map<String, Object> payload,
                Instant requestedAt
        ) {
            this.modelKey = nullIfBlank(modelKey);
            this.userId = requireNonBlank(userId, "userId");
            this.featureSnapshotId = requireNonBlank(featureSnapshotId, "featureSnapshotId");
            this.creditDictionaryVersionId = nullIfBlank(creditDictionaryVersionId);
            this.policyVersion = nullIfBlank(policyVersion);
            this.payload = Collections.unmodifiableMap(new LinkedHashMap<>(payload == null ? Map.of() : payload));
            this.requestedAt = Objects.requireNonNull(requestedAt, "requestedAt must not be null");
        }

        public String getModelKey() {
            return modelKey;
        }

        public String getUserId() {
            return userId;
        }

        public String getFeatureSnapshotId() {
            return featureSnapshotId;
        }

        public String getCreditDictionaryVersionId() {
            return creditDictionaryVersionId;
        }

        public String getPolicyVersion() {
            return policyVersion;
        }

        public Map<String, Object> getPayload() {
            return payload;
        }

        public Instant getRequestedAt() {
            return requestedAt;
        }

        @Override
        public String toString() {
            return "MlScoreRequest{" +
                    "modelKey='" + modelKey + '\'' +
                    ", userId='" + userId + '\'' +
                    ", featureSnapshotId='" + featureSnapshotId + '\'' +
                    ", creditDictionaryVersionId='" + creditDictionaryVersionId + '\'' +
                    ", policyVersion='" + policyVersion + '\'' +
                    ", payloadKeys=" + payload.keySet() +
                    ", requestedAt=" + requestedAt +
                    '}';
        }
    }

    /**
     * (Version 1) 내부에서 "ML 점수 요청"에 필요한 최소 입력만 받아 요청으로 변환합니다.
     *
     * @param userId 사용자 ID(내부는 NUMBER일 가능성이 높으므로 toString된 값 권장)
     * @param featureSnapshotId 피처 스냅샷 ID(필수)
     * @param creditDictionaryVersionId 신용사전 버전(선택)
     * @param policyVersion 정책 버전(선택)
     * @param features 피처 값(가능하면 "정규화된" 피처를 전달)
     * @param options 옵션(임계치/모드 등, 스펙 확정 전까지 Map으로 유지)
     * @param requestedAt 요청 시각(결정론 목적: 호출자가 전달 권장. null이면 즉시 예외)
     */
    public MlScoreRequest buildScoreRequest(
            String userId,
            String featureSnapshotId,
            String creditDictionaryVersionId,
            String policyVersion,
            Map<String, Object> features,
            Map<String, Object> options,
            Instant requestedAt
    ) {
        Map<String, Object> payload = new LinkedHashMap<>();
        if (features != null && !features.isEmpty()) payload.put("features", sanitizeMap(features));
        if (options != null && !options.isEmpty()) payload.put("options", sanitizeMap(options));

        // modelKey는 외부 확정 전이므로 강제하지 않음
        return new MlScoreRequest(
                null,
                userId,
                featureSnapshotId,
                creditDictionaryVersionId,
                policyVersion,
                payload,
                Objects.requireNonNull(requestedAt, "requestedAt must not be null")
        );
    }

    // ----------------------
    // Internal helpers
    // ----------------------

    private static Map<String, Object> sanitizeMap(Map<String, Object> input) {
        Map<String, Object> out = new LinkedHashMap<>();
        for (Map.Entry<String, Object> e : input.entrySet()) {
            String k = nullIfBlank(e.getKey());
            if (k == null) continue;
            Object v = e.getValue();
            // null은 제거(외부 API에 불필요한 키 전송 방지)
            if (v != null) out.put(k, v);
        }
        return out;
    }

    private static String requireNonBlank(String value, String fieldName) {
        String v = nullIfBlank(value);
        if (v == null) {
            throw new IllegalArgumentException(fieldName + " must not be blank.");
        }
        return v;
    }

    private static String nullIfBlank(String value) {
        if (value == null) return null;
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }
}
