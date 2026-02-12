package com.ccksy.loan.domain.product.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Provider {
    private Long providerId;
    private String finCoNo;
    private String companyName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
