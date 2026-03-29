package com.ccksy.loan.domain.admin.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminLoginResponse {
    private String accessToken;
    private long expiresIn;
    private String id;
    private String role;
}
