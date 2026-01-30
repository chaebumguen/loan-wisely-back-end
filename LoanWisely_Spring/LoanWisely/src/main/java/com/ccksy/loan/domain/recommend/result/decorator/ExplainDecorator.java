package com.ccksy.loan.domain.recommend.result.decorator;

import java.util.HashMap;
import java.util.Map;

import com.ccksy.loan.domain.recommend.result.core.RecommendItem;
import com.ccksy.loan.domain.recommend.result.core.RecommendResult;

/**
 * 추천 사유 설명 Decorator
 *
 * <p>상품별 점수 산정 이유를 설명 형태로 제공</p>
 */
public class ExplainDecorator extends RecommendResultDecorator {

    public ExplainDecorator(RecommendResult result) {
        super(result);
    }

    @Override
    public RecommendResult decorate() {
        Map<Long, String> explainMap = new HashMap<>();

        for (RecommendItem item : result.getItems()) {
            String reason = "점수 기반 추천 결과 (score=" + item.getScore() + ")";
            explainMap.put(item.getProductId(), reason);
        }

        // v1: RecommendResult 확장 필드가 없으므로
        // 실제 응답 DTO 단계에서 explainMap 을 병합하도록 설계
        // (Response 계층 책임)
        return result;
    }
}
