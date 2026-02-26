package com.ccksy.loan.domain.admin.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AdminVerifyResponse {
    private String id;
    private List<String> roles;
}
