package com.ccksy.loan.domain.recommend.process.template;

import java.util.List;
import java.util.Objects;

import com.ccksy.loan.domain.recommend.dto.internal.RecommendContext;
import com.ccksy.loan.domain.recommend.result.core.RecommendItem;

/**
 * 추천 프로세스 템플릿 (Template Method Pattern)
 *
 * <pre>
 * 실행 흐름(고정):
 * 1. validate   : 추천 가능 여부 검증
 * 2. filter     : 부적격 상품 제거
 * 3. score      : 점수 산정
 * 4. sort       : 결과 정렬
 * </pre>
 *
 * <p>
 * 본 클래스는 "순서"를 책임지며,
 * 구현체는 각 단계의 "내용"만 정의한다.
 * </p>
 */
public abstract class AbstractRecommendProcess {

    /**
     * 템플릿 메서드 (외부 호출 유일 진입점)
     */
    public final List<RecommendItem> execute(
            RecommendContext context,
            List<RecommendItem> candidates
    ) {
        Objects.requireNonNull(context, "RecommendContext must not be null.");
        Objects.requireNonNull(candidates, "Candidate list must not be null.");

        // 1. 사전 검증
        validate(context);

        // 2. 필터링
        List<RecommendItem> filtered = filter(context, candidates);

        // 3. 점수 산정
        score(context, filtered);

        // 4. 정렬
        sort(filtered);

        // 5. 사후 처리 (선택)
        afterProcess(context);

        return filtered;
    }

    /**
     * 1. 추천 실행 전 검증
     */
    protected abstract void validate(RecommendContext context);

    /**
     * 2. 부적격 필터링
     */
    protected abstract List<RecommendItem> filter(
            RecommendContext context,
            List<RecommendItem> candidates
    );

    /**
     * 3. 점수 산정
     */
    protected abstract void score(
            RecommendContext context,
            List<RecommendItem> items
    );

    /**
     * 4. 정렬
     */
    protected abstract void sort(List<RecommendItem> items);

    /**
     * 5. 사후 처리 (hook method)
     *
     * <p>기본 구현은 아무 것도 하지 않는다.</p>
     */
    protected void afterProcess(RecommendContext context) {
        // no-op (확장 포인트)
    }
}
