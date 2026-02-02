package com.ccksy.loan.adapter.ml;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;

/**
 * MlScoreAdapter
 * - 외부 ML 서버 응답(JSON 등)을 내부에서 쓰기 쉬운 점수/근거 형태로 변환합니다.
 * - (Version 1) 응답 스키마가 확정되지 않았으므로, "키 후보 탐색" 방식으로 안전하게 매핑합니다.
 *
 * 결정론/재현성:
 * - 동일 JsonNode 입력이면 동일 결과를 반환하도록 순수 파싱 로직 위주로 구성합니다.
 */
public class MlScoreAdapter {

    /**
     * 내부에서 사용할 ML 점수 결과(Adapter 내부 전용)
     * - 도메인 엔티티/DB 저장 모델로 확정되면 이 클래스는 제거/대체 가능합니다.
     */
    public static final class MlScoreResult {
        private final BigDecimal score;               // 예: 0~1 또는 0~100 (외부에 따라 다름)
        private final String scoreScale;              // 예: "0_1", "0_100" 등 (없으면 null)
        private final String modelVersion;            // 외부 모델 버전(있다면)
        private final Instant scoredAt;               // 점수 산출 시각(없으면 null)
        private final List<Contribution> contributions; // feature 기여도 (있으면)
        private final Map<String, Object> extras;     // 확장

        public MlScoreResult(
                BigDecimal score,
                String scoreScale,
                String modelVersion,
                Instant scoredAt,
                List<Contribution> contributions,
                Map<String, Object> extras
        ) {
            this.score = score;
            this.scoreScale = nullIfBlank(scoreScale);
            this.modelVersion = nullIfBlank(modelVersion);
            this.scoredAt = scoredAt;
            this.contributions = Collections.unmodifiableList(contributions == null ? List.of() : contributions);
            this.extras = Collections.unmodifiableMap(new LinkedHashMap<>(extras == null ? Map.of() : extras));
        }

        public BigDecimal getScore() { return score; }
        public String getScoreScale() { return scoreScale; }
        public String getModelVersion() { return modelVersion; }
        public Instant getScoredAt() { return scoredAt; }
        public List<Contribution> getContributions() { return contributions; }
        public Map<String, Object> getExtras() { return extras; }

        @Override
        public String toString() {
            return "MlScoreResult{" +
                    "score=" + score +
                    ", scoreScale='" + scoreScale + '\'' +
                    ", modelVersion='" + modelVersion + '\'' +
                    ", scoredAt=" + scoredAt +
                    ", contributions=" + contributions +
                    ", extrasKeys=" + extras.keySet() +
                    '}';
        }
    }

    /**
     * 기여도(설명) 항목
     */
    public static final class Contribution {
        private final String featureKey;
        private final BigDecimal contribution; // 양/음 기여 가능
        private final String reason;           // 선택

        public Contribution(String featureKey, BigDecimal contribution, String reason) {
            this.featureKey = requireNonBlank(featureKey, "featureKey");
            this.contribution = contribution;
            this.reason = nullIfBlank(reason);
        }

        public String getFeatureKey() { return featureKey; }
        public BigDecimal getContribution() { return contribution; }
        public String getReason() { return reason; }

        @Override
        public String toString() {
            return "Contribution{featureKey='" + featureKey + "', contribution=" + contribution + ", reason='" + reason + "'}";
        }
    }

    /**
     * 외부 ML 응답(JsonNode)에서 점수 결과를 파싱합니다.
     *
     * @param node 외부 응답 JSON
     * @param scoreKeys 점수 필드 후보 키 (예: ["score","riskScore","prediction"])
     * @param modelVersionKeys 모델 버전 후보 키 (예: ["modelVersion","version"])
     * @param scoredAtKeys 산출 시각 후보 키 (예: ["scoredAt","timestamp"])
     * @param contributionArrayKeys 기여도 배열 후보 키 (예: ["contributions","shapValues","explain"])
     */
    public MlScoreResult fromJsonNode(
            JsonNode node,
            String[] scoreKeys,
            String[] modelVersionKeys,
            String[] scoredAtKeys,
            String[] contributionArrayKeys
    ) {
        Objects.requireNonNull(node, "node must not be null");

        BigDecimal score = firstDecimal(node, scoreKeys);
        String modelVersion = firstText(node, modelVersionKeys);
        Instant scoredAt = firstInstant(node, scoredAtKeys);

        List<Contribution> contributions = parseContributions(node, contributionArrayKeys);

        Map<String, Object> extras = new LinkedHashMap<>();
        if (score == null) extras.put("warning", "score_missing_or_unparseable");

        // 스케일은 외부 확정 전이라 추정하지 않음(결정론/오해 방지)
        return new MlScoreResult(score, null, modelVersion, scoredAt, contributions, extras);
    }

    // ----------------------
    // Parsing helpers
    // ----------------------

    private static List<Contribution> parseContributions(JsonNode node, String[] contributionArrayKeys) {
        if (contributionArrayKeys == null) return List.of();

        JsonNode arr = null;
        for (String k : contributionArrayKeys) {
            if (k == null || k.isBlank()) continue;
            JsonNode v = node.get(k);
            if (v != null && v.isArray()) {
                arr = v;
                break;
            }
        }
        if (arr == null || !arr.isArray()) return List.of();

        List<Contribution> out = new ArrayList<>();
        for (JsonNode item : arr) {
            // 다양한 형태를 허용:
            // 1) { "feature":"x", "value":0.12, "reason":"..." }
            // 2) { "key":"x", "contribution":-0.03 }
            // 3) ["x", 0.12]
            String feature = null;
            BigDecimal contrib = null;
            String reason = null;

            if (item.isObject()) {
                feature = firstText(item, new String[]{"feature", "key", "name"});
                contrib = firstDecimal(item, new String[]{"contribution", "value", "weight"});
                reason = firstText(item, new String[]{"reason", "desc", "explanation"});
            } else if (item.isArray() && item.size() >= 2) {
                feature = nullIfBlank(item.get(0).asText(null));
                contrib = parseDecimalOrNull(item.get(1));
            }

            if (feature != null) {
                out.add(new Contribution(feature, contrib, reason));
            }
        }
        return out;
    }

    private static BigDecimal firstDecimal(JsonNode node, String[] keys) {
        if (keys == null || keys.length == 0) return null;
        for (String k : keys) {
            if (k == null || k.isBlank()) continue;
            JsonNode v = node.get(k);
            BigDecimal d = parseDecimalOrNull(v);
            if (d != null) return d;
        }
        return null;
    }

    private static String firstText(JsonNode node, String[] keys) {
        if (keys == null || keys.length == 0) return null;
        for (String k : keys) {
            if (k == null || k.isBlank()) continue;
            JsonNode v = node.get(k);
            if (v == null || v.isNull()) continue;
            String t = v.asText();
            if (t != null && !t.trim().isEmpty()) return t.trim();
        }
        return null;
    }

    private static Instant firstInstant(JsonNode node, String[] keys) {
        String raw = firstText(node, keys);
        if (raw == null) return null;

        // 외부가 ISO-8601을 준다는 보장은 없으므로, 파싱 실패 시 null 처리(임의 추정 금지)
        try {
            return Instant.parse(raw);
        } catch (Exception ignore) {
            return null;
        }
    }

    private static BigDecimal parseDecimalOrNull(JsonNode node) {
        if (node == null || node.isNull()) return null;

        if (node.isNumber()) {
            return node.decimalValue().setScale(6, RoundingMode.HALF_UP);
        }

        if (node.isTextual()) {
            String s = nullIfBlank(node.asText());
            if (s == null) return null;

            // 숫자/소수점/부호 외 제거
            String normalized = s.replaceAll("[^0-9.\\-+]", "");
            if (normalized.isBlank()) return null;

            try {
                return new BigDecimal(normalized).setScale(6, RoundingMode.HALF_UP);
            } catch (NumberFormatException ex) {
                return null;
            }
        }

        return null;
    }

    private static String requireNonBlank(String value, String fieldName) {
        String v = nullIfBlank(value);
        if (v == null) throw new IllegalArgumentException(fieldName + " must not be blank.");
        return v;
    }

    private static String nullIfBlank(String value) {
        if (value == null) return null;
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }
}
