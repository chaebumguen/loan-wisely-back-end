package com.ccksy.loan.domain.recommend.result.decorator;

import java.util.ArrayList;
import java.util.List;

import com.ccksy.loan.domain.recommend.result.core.RecommendItem;
import com.ccksy.loan.domain.recommend.result.core.RecommendResult;

/**
 * 주의/경고 메시지 Decorator
 *
 * <p>금리/위험도 기준으로 사용자 주의 문구 생성</p>
 */
public class WarningDecorator extends RecommendResultDecorator {

    public WarningDecorator(RecommendResult result) {
        super(result);
    }

    @Override
    public RecommendResult decorate() {
        List<String> warnings = new ArrayList<>();

        for (RecommendItem item : result.getItems()) {
            if (item.getInterestRate() > 15.0) {
                warnings.add("금리가 높은 상품이 포함되어 있습니다.");
                break;
            }
        }

        // v1: Core 확장 없음 → Response 계층에서 활용
        return result;
    }
}
