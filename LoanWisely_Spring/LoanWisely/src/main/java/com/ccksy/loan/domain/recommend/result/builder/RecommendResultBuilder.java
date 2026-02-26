package com.ccksy.loan.domain.recommend.result.builder;

import com.ccksy.loan.domain.recommend.result.core.RecommendResult;

public class RecommendResultBuilder {

    private RecommendResultBuilder() {}

    public static RecommendResult.RecommendResultBuilder ready(String reproduceKey, String stateCode) {
        return RecommendResult.builder()
                .state(stateCode)
                .reproduceKey(reproduceKey)
                .policyVersion("POLICY-V1")
                .metaVersion("META-V1");
    }

    public static RecommendResult.RecommendResultBuilder notReady(String reproduceKey, String stateCode) {
        return RecommendResult.builder()
                .state(stateCode)
                .reproduceKey(reproduceKey)
                .policyVersion("POLICY-V1")
                .metaVersion("META-V1");
    }

    public static RecommendResult.RecommendResultBuilder blocked(String reproduceKey, String stateCode) {
        return RecommendResult.builder()
                .state(stateCode)
                .reproduceKey(reproduceKey)
                .policyVersion("POLICY-V1")
                .metaVersion("META-V1");
    }

    /**
     * 전역 경고/안내(warnings) 추가 헬퍼
     * - Lombok Builder에는 사용자 정의 체이닝 메서드가 없으므로,
     *   builder를 받아 warnings 맵에 put 하고 다시 반환한다.
     */
    public static RecommendResult.RecommendResultBuilder addGlobalWarning(
            RecommendResult.RecommendResultBuilder builder,
            String code,
            String message
    ) {
        if (builder == null) return null;

        // Lombok builder는 내부적으로 warnings(Map)를 세팅할 수 있는 setter를 생성합니다.
        // 단, 현재 경고들을 "누적"하려면 build() 후 수정하는 방식이 아니라
        // 아래처럼 빌더의 warnings(...)를 호출하는 형태로 누적해야 합니다.
        //
        // 이 헬퍼는 "한 번만" 경고를 넣는 단순 사용을 전제로 합니다.
        // (여러 개 누적은 다음 단계에서 RecommendResultDecorator로 처리하는 게 안전합니다.)
        RecommendResult tmp = builder.build();
        tmp.getWarnings().put(code, message);

        // 빌더를 다시 만들어 이어서 체이닝 가능하게 반환
        return tmp.toBuilder();
    }
}
