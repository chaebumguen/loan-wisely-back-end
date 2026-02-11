package com.ccksy.loan.domain.user.dto.response;

import com.ccksy.loan.domain.user.entity.UserProfile;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserProfileResponse {

    private Long userId;
    private Integer inputLevel;

    private Integer age;
    private Long incomeYear;
    private String gender;

    private String employmentType;
    private String residenceType;

    private Long debtTotal;
    private Integer existingLoanCount;
    private String loanPurpose;

    private String inputStateCode;
    private String isActive;
    private LocalDateTime createdAt;

    public static UserProfileResponse from(UserProfile entity) {
        return UserProfileResponse.builder()
                .userId(entity.getUserId())
                .inputLevel(entity.getInputLevel())
                .age(entity.getAge())
                .incomeYear(entity.getIncomeYear())
                .gender(entity.getGender())
                .employmentType(entity.getEmploymentType())
                .residenceType(entity.getResidenceType())
                .debtTotal(entity.getDebtTotal())
                .existingLoanCount(entity.getExistingLoanCount())
                .loanPurpose(entity.getLoanPurpose())
                .inputStateCode(entity.getInputStateCode())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
