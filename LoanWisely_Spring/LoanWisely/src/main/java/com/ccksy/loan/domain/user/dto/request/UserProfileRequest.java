package com.ccksy.loan.domain.user.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class UserProfileRequest {

    /**
     * LV1 필수 입력
     */
    @NotNull
    @Min(0)
    private Integer age;

    @NotNull
    @PositiveOrZero
    private Long incomeYear;

    @NotNull
    private String gender;

    /**
     * LV2 선택 입력
     */
    private String employmentType;
    private String residenceType;

    /**
     * LV3 선택 입력
     */
    private String loanPurpose;
    private Long totalDebt;
    private Integer existingLoanCount;

    protected UserProfileRequest() {
        // Jackson 역직렬화 전용
    }

    public Integer getAge() {
        return age;
    }

    public Long getIncomeYear() {
        return incomeYear;
    }

    public String getGender() {
        return gender;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public String getResidenceType() {
        return residenceType;
    }

    public String getLoanPurpose() {
        return loanPurpose;
    }

    public Long getTotalDebt() {
        return totalDebt;
    }

    public Integer getExistingLoanCount() {
        return existingLoanCount;
    }
}
