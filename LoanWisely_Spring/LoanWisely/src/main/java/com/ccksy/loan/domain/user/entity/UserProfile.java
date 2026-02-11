package com.ccksy.loan.domain.user.entity;

import com.ccksy.loan.domain.user.dto.request.UserProfileRequest;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 사용자 입력/이력 엔티티
 * - 이력 불변: 업데이트가 아니라 신규 이력 추가 전제
 * - 활성 플래그로 최신 유효 레코드 식별
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    // PK는 DB 시퀀스/트리거 또는 Mapper에서 처리(현재는 필드만 정의)
    private Long profileId;

    private Long userId;

    /**
     * LV1~LV3
     */
    private Integer inputLevel;

    // LV1
    private Integer age;
    private Long incomeYear;
    private String gender;

    // LV2
    private String employmentType;
    private String residenceType;

    // LV3
    private Long debtTotal;
    private Integer existingLoanCount;
    private String loanPurpose;

    // 상태/이력
    private String inputStateCode;
    private String isActive;     // "Y"/"N"
    private LocalDateTime createdAt;

    public static UserProfile from(UserProfileRequest req) {
        return UserProfile.builder()
                .userId(req.getUserId())
                .inputLevel(req.getInputLevel())
                .age(req.getAge())
                .incomeYear(req.getIncomeYear())
                .gender(req.getGender())
                .employmentType(req.getEmploymentType())
                .residenceType(req.getResidenceType())
                .debtTotal(req.getDebtTotal())
                .existingLoanCount(req.getExistingLoanCount())
                .loanPurpose(req.getLoanPurpose())
                .build();
    }
}
