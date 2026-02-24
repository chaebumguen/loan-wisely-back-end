package com.ccksy.loan.domain.user.dto.request;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Unified DTO for saving user input (LV1~LV3).
 * Null is allowed for fields not provided yet.
 */
@Getter
@Setter
@NoArgsConstructor
public class UserProfileRequest {

    private Long userId;

    /**
     * Input level (1~3).
     */
    @NotNull
    private Integer inputLevel;

    // LV1 (required)
    @PositiveOrZero
    private Integer age;
    @PositiveOrZero
    private Long incomeYear;
    private String gender;

    // LV2 (optional)
    private String employmentType;
    private String residenceType;

    // LV3 (optional)
    @PositiveOrZero
    private Long debtTotal;
    @PositiveOrZero
    private Integer existingLoanCount;
    private String loanPurpose;

    public void assertRequiredFields() {
        if (inputLevel == null || inputLevel < 1 || inputLevel > 3) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "inputLevel must be between 1 and 3");
        }

        if (age == null || incomeYear == null || gender == null) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_FAILED,
                    "LV1 required fields (age, incomeYear, gender) are missing"
            );
        }
    }
}
