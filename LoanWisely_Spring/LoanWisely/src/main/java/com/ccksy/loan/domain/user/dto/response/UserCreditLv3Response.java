package com.ccksy.loan.domain.user.dto.response;

import com.ccksy.loan.domain.user.entity.UserCreditLv3;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserCreditLv3Response {

    private Long userId;
    private String loanPurpose;
    private Long totalDebt;
    private Integer existingLoanCount;
    private LocalDateTime createdAt;

    public static UserCreditLv3Response from(UserCreditLv3 entity) {
        return UserCreditLv3Response.builder()
                .userId(entity.getUserId())
                .loanPurpose(entity.getLoanPurpose())
                .totalDebt(entity.getTotalDebt())
                .existingLoanCount(entity.getExistingLoanCount())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
