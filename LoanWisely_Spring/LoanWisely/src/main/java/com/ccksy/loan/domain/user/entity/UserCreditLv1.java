package com.ccksy.loan.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 신용 입력 LV1 엔티티
 * - 설계서 기준 USER_CREDIT_LV1 테이블 대응
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserCreditLv1 {

    private Long lv1Id;
    private Long userId;
    private Integer age;
    private Long incomeYear;
    private String gender;
    private String isActive;
    private LocalDateTime createdAt;
}
