package com.ccksy.loan.domain.consent.dto.request;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * LV별 동의 입력 DTO
 * - consentLevel: 1~3
 * - consentGiven: true(동의) / false(미동의)
 */
@Getter
@Setter
@NoArgsConstructor
public class UserConsentRequest {

    private Long userId;

    @NotNull
    private Integer consentLevel;

    @NotNull
    private Boolean consentGiven;

    public void assertRequiredFields() {
        if (consentLevel == null || consentLevel < 1 || consentLevel > 3) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "consentLevel은 1~3 범위여야 합니다.");
        }
        if (consentGiven == null) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "consentGiven은 필수입니다.");
        }
    }
}
