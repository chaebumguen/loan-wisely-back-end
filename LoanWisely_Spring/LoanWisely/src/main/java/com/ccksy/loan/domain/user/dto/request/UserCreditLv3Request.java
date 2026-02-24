package com.ccksy.loan.domain.user.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserCreditLv3Request {

    private Long userId;

    private String loanPurpose;
    @PositiveOrZero
    private Long totalDebt;
    @PositiveOrZero
    private Integer existingLoanCount;
}
