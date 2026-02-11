package com.ccksy.loan.domain.admin.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AdminUser {
    private Long adminId;
    private String username;
    private String passwordHash;
    private String status;
    private LocalDateTime createdAt;
}
