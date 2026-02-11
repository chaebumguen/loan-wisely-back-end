package com.ccksy.loan.domain.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserCreditLv3Request {

    private Long userId;

    private String loanPurpose;
    private Long totalDebt;
    private Integer existingLoanCount;
}
