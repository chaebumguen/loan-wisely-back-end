package com.ccksy.loan.domain.user.dto.response;

import com.ccksy.loan.domain.user.entity.UserCreditLv1;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserCreditLv1Response {

    private Long userId;
    private Integer age;
    private Long incomeYear;
    private String gender;
    private LocalDateTime createdAt;

    public static UserCreditLv1Response from(UserCreditLv1 entity) {
        return UserCreditLv1Response.builder()
                .userId(entity.getUserId())
                .age(entity.getAge())
                .incomeYear(entity.getIncomeYear())
                .gender(entity.getGender())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
