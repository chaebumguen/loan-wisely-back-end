package com.ccksy.loan.domain.user.dto.response;

import com.ccksy.loan.domain.user.entity.UserCreditLv2;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserCreditLv2Response {

    private Long userId;
    private String employmentType;
    private String residenceType;
    private LocalDateTime createdAt;

    public static UserCreditLv2Response from(UserCreditLv2 entity) {
        return UserCreditLv2Response.builder()
                .userId(entity.getUserId())
                .employmentType(entity.getEmploymentType())
                .residenceType(entity.getResidenceType())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
