package com.ccksy.loan.domain.user.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuth {
    private Long userId;
    private String username;
    private String passwordHash;
    private String status;
    private Integer failLoginCount;
    private String isLocked;
    private LocalDateTime passwordUpdatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}
