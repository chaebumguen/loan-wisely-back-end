package com.ccksy.loan.domain.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserCreditLv2Request {

    private Long userId;

    private String employmentType;
    private String residenceType;
}
