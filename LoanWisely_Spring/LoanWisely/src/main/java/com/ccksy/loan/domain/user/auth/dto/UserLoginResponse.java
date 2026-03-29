package com.ccksy.loan.domain.user.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginResponse {
    private Long userId;
    private String username;
    private String accessToken;
    private Long expiresInSeconds;
}
