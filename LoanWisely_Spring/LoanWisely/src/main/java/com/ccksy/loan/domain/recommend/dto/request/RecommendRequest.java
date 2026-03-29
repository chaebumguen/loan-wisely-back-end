package com.ccksy.loan.domain.recommend.dto.request;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 추천 요청(외부 입력)
 * - v1: 최소 입력만 받음
 * - LV는 user_profile 쪽 최신 유효 이력에서 식별 가능해야 함(추후 프로세스에서 확정)
 */
@Getter
@Setter
@NoArgsConstructor
public class RecommendRequest {

    private Long userId;

    /**
     * 요청 옵션: 사용자가 제공했다고 주장하는 LV(1~3)
     * - v1에서는 참고값으로만 받고, 실제 적용 LV는 내부 로딩 결과로 확정하는 것을 권장
     */
    private Integer requestedInputLevel;

    /**
     * 호출 추적용(프론트/게이트웨이에서 전달)
     */
    private String requestTraceId;

    public void assertRequiredFields() {
        if (userId == null) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "userId는 필수입니다.");
        }
        if (requestedInputLevel != null && (requestedInputLevel < 1 || requestedInputLevel > 3)) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "requestedInputLevel은 1~3 범위여야 합니다.");
        }
    }
}
