package com.ccksy.loan.domain.recommend.policy.eligibility;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.ccksy.loan.domain.recommend.filter.model.FilterContext;

/**
 * 기본 자격 정책 구현체.
 *
 * <p>현재 버전(v1)에서는 다음 원칙으로 동작한다.</p>
 * <ul>
 *   <li>컨텍스트 null 방지</li>
 *   <li>필수 속성(계약) 최소 검증: reflection 기반(컴파일 결합도 최소화)</li>
 * </ul>
 *
 * <p>주의: reflection 기반 검증은 "필수 getter 계약"이 깨졌을 때 빠르게 실패(fail-fast)시키기 위함이다.
 * 팀 내 공통 입력/공통 DTO 계약이 확정되면 직접 필드/게터 접근 방식으로 전환하는 것을 권장한다.</p>
 */
public class DefaultEligibilityPolicy implements EligibilityPolicy {

    /**
     * v1 최소 계약(필수 getter) 목록.
     * - 실제 FilterContext 설계 확정에 따라 조정 가능
     */
    private static final List<String> REQUIRED_GETTERS_PRE = Arrays.asList(
            "getUserId",
            "getPurpose"
    );

    @Override
    public void preValidate(FilterContext context) {
        if (context == null) {
            throw new IllegalArgumentException("FilterContext must not be null.");
        }

        // 필수 계약(게터) 존재/값 검증
        for (String getterName : REQUIRED_GETTERS_PRE) {
            Object value = invokeGetter(context, getterName);
            if (value == null) {
                throw new IllegalArgumentException(
                        "Required value is null. getter=" + getterName + ", contextType=" + context.getClass().getName()
                );
            }

            // 문자열 계열이면 공백도 방지
            if (value instanceof String && ((String) value).trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "Required value is blank. getter=" + getterName + ", contextType=" + context.getClass().getName()
                );
            }
        }
    }

    @Override
    public void postValidate(FilterContext context) {
        // v1: 후처리 공통 자격 점검 없음(확장 포인트)
        Objects.requireNonNull(context, "FilterContext must not be null.");
    }

    private Object invokeGetter(FilterContext context, String getterName) {
        try {
            Method m = context.getClass().getMethod(getterName);
            return m.invoke(context);
        } catch (NoSuchMethodException e) {
            // 계약 위반: 팀 공통 DTO/컨텍스트가 필수 게터를 제공하지 않음
            throw new IllegalStateException(
                    "Required getter not found on FilterContext. getter=" + getterName + ", contextType=" + context.getClass().getName(),
                    e
            );
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to invoke required getter. getter=" + getterName + ", contextType=" + context.getClass().getName(),
                    e
            );
        }
    }
}
