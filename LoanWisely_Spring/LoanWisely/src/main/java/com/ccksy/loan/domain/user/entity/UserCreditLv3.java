package com.ccksy.loan.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 신용 입력 LV3 엔티티
 * - 설계서 기준 USER_CREDIT_LV3 테이블 대응
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserCreditLv3 {

    private Long lv3Id;
    private Long userId;
    private String loanPurpose;
    private Long totalDebt;
    private Integer existingLoanCount;
    private String isActive;
    private LocalDateTime createdAt;
}
