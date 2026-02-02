// FILE: domain/recommend/policy/scoring/CompositeScoreStrategy.java
package com.ccksy.loan.domain.recommend.policy.scoring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.ccksy.loan.domain.recommend.dto.internal.RecommendContext;
import com.ccksy.loan.domain.recommend.result.core.RecommendItem;

/**
 * (Strategy) 여러 점수 전략 조합 (v1)
 *
 * v1 원칙:
 * - 전략 목록 순서가 결과에 영향 -> 생성 시 전달된 순서를 그대로 사용(결정론)
 * - weights는 고정 값만 허용(런타임 랜덤 금지)
 */
public final class CompositeScoreStrategy implements ScoreStrategy {

    public static final class Entry {
        private final ScoreStrategy strategy;
        private final double weight;

        public Entry(ScoreStrategy strategy, double weight) {
            this.strategy = Objects.requireNonNull(strategy, "strategy");
            this.weight = weight;
        }

        public ScoreStrategy getStrategy() {
            return strategy;
        }

        public double getWeight() {
            return weight;
        }
    }

    private final List<Entry> entries;

    public CompositeScoreStrategy(List<Entry> entries) {
        Objects.requireNonNull(entries, "entries");
        this.entries = Collections.unmodifiableList(new ArrayList<>(entries));
        if (this.entries.isEmpty()) {
            throw new IllegalArgumentException("entries must not be empty.");
        }
    }

    @Override
    public String id() {
        // 구성 요소를 고정 순서로 직렬화(재현키에 포함 가능)
        StringBuilder sb = new StringBuilder("CompositeScoreStrategy:v1:");
        for (int i = 0; i < entries.size(); i++) {
            Entry e = entries.get(i);
            if (i > 0) sb.append("|");
            sb.append(e.getStrategy().id()).append("@").append(trimDouble(e.getWeight()));
        }
        return sb.toString();
    }

    @Override
    public double score(RecommendContext ctx, RecommendItem item) {
        Objects.requireNonNull(ctx, "ctx");
        Objects.requireNonNull(item, "item");

        double sum = 0.0d;
        for (Entry e : entries) {
            sum += e.getWeight() * e.getStrategy().score(ctx, item);
        }
        return sum;
    }

    private static String trimDouble(double v) {
        String s = Double.toString(v);
        if (s.endsWith(".0")) return s.substring(0, s.length() - 2);
        return s;
    }
}

/**
 * v1 유틸(추가 파일 생성 금지에 따라 동일 파일(폴더) 내 package-private로 포함)
 *
 * - RecommendContext/options 안전 파싱
 * - RecommendItem 리플렉션 접근(getXxx / setScore) 보조
 */
final class ScoreStrategyUtil {

    private ScoreStrategyUtil() {}

    static String optText(RecommendContext ctx, String key) {
        Object v = ctx.getOptions().get(key);
        if (v == null) return null;
        String s = String.valueOf(v).trim();
        return s.isEmpty() ? null : s;
    }

    static Integer optInt(RecommendContext ctx, String key) {
        Object v = ctx.getOptions().get(key);
        return asInt(v);
    }

    static boolean equalsIgnoreSpace(String a, String b) {
        if (a == null || b == null) return false;
        return a.trim().equalsIgnoreCase(b.trim());
    }

    static Integer tryGetInt(Object target, String... methodNames) {
        Object v = tryInvoke(target, methodNames);
        return asInt(v);
    }

    static String tryGetText(Object target, String... methodNames) {
        Object v = tryInvoke(target, methodNames);
        if (v == null) return null;
        String s = String.valueOf(v).trim();
        return s.isEmpty() ? null : s;
    }

    static void trySetScore(Object target, double score) {
        if (target == null) return;

        // setScore(double)
        if (tryInvokeSetter(target, "setScore", double.class, score)) return;
        // setScore(Double)
        if (tryInvokeSetter(target, "setScore", Double.class, Double.valueOf(score))) return;

        // setScore(BigDecimal)
        try {
            Class<?> bd = Class.forName("java.math.BigDecimal");
            Object big = bd.getMethod("valueOf", double.class).invoke(null, score);
            tryInvokeSetter(target, "setScore", bd, big);
        } catch (Exception ignored) {
            // no-op
        }
    }

    private static boolean tryInvokeSetter(Object target, String name, Class<?> paramType, Object arg) {
        try {
            target.getClass().getMethod(name, paramType).invoke(target, arg);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static Object tryInvoke(Object target, String... methodNames) {
        if (target == null || methodNames == null) return null;
        for (String m : methodNames) {
            if (m == null || m.isBlank()) continue;
            try {
                return target.getClass().getMethod(m).invoke(target);
            } catch (Exception ignored) {
                // try next
            }
        }
        return null;
    }

    private static Integer asInt(Object v) {
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).intValue();
        try {
            String s = String.valueOf(v).trim();
            if (s.isEmpty()) return null;
            return Integer.parseInt(s);
        } catch (Exception ignored) {
            return null;
        }
    }
}
