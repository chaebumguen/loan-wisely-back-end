package com.ccksy.loan.domain.admin.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminLoginRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
