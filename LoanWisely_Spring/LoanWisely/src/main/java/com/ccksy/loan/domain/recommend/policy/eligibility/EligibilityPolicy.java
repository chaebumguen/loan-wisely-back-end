package com.ccksy.loan.domain.recommend.policy.eligibility;

import com.ccksy.loan.domain.recommend.filter.model.FilterContext;

/**
 * (선택) 체인 전/후 공통 자격 정책.
 *
 * <p>Chain of Responsibility 필터 체인에서 처리하기 애매한 "공통 자격 요건"을
 * 체인 실행 전/후로 분리하여 점검한다.</p>
 *
 * <p>정책의 실패는 예외로 처리하여 상위(프로세스/커맨드 핸들러/파사드)에서
 * 표준 응답/로그로 변환하는 것을 권장한다.</p>
 */
public interface EligibilityPolicy {

    /**
     * 필터 체인 실행 전 공통 자격 점검.
     * @param context 추천 판단에 필요한 입력 컨텍스트
     */
    void preValidate(FilterContext context);

    /**
     * 필터 체인 실행 후 공통 자격 점검(선택).
     * @param context 추천 판단에 필요한 입력 컨텍스트
     */
    void postValidate(FilterContext context);
}
