package com.ccksy.loan.domain.product.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderUrl {
    private String finCoNo;
    private String homepageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
