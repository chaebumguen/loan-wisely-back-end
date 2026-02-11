package com.ccksy.loan.domain.user.dto.request;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * LV1~LV3 입력을 단일 DTO로 수용 (미제공은 null 허용)
 * - LV1: age, incomeYear, gender (필수)
 * - LV2/LV3: employmentType, residenceType, debtTotal, existingLoanCount, loanPurpose (선택/동의 기반)
 */
@Getter
@Setter
@NoArgsConstructor
public class UserProfileRequest {

    private Long userId;

    /**
     * 입력 레벨 (1~3)
     */
    @NotNull
    private Integer inputLevel;

    // LV1 (필수)
    private Integer age;
    private Long incomeYear;
    private String gender;

    // LV2 (선택)
    private String employmentType;
    private String residenceType;

    // LV3 (선택: 동의 필요는 consent 모듈에서 통제 예정)
    private Long debtTotal;
    private Integer existingLoanCount;
    private String loanPurpose;

    public void assertRequiredFields() {
        if (inputLevel == null || inputLevel < 1 || inputLevel > 3) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "inputLevel은 1~3 범위여야 합니다.");
        }

        // LV 규칙: 상위 LV 입력은 하위 LV 포함 상태로만 허용 (현재는 LV1 필수만 강제)
        if (age == null || incomeYear == null || gender == null) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "LV1 필수값(age, incomeYear, gender)이 누락되었습니다.");
        }
    }
}
