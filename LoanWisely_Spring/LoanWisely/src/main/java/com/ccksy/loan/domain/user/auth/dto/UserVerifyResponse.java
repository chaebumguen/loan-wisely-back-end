package com.ccksy.loan.domain.user.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserVerifyResponse {
    private Long userId;
    private String username;
}
