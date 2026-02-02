package com.ccksy.loan.domain.user.entity;

import java.time.LocalDateTime;

/**
 * 사용자 프로필 이력 엔티티 (Append-only)
 * 판단/계산/상태 로직 없음
 */
public class UserProfile {

    private Long id;
    private Long userId;

    /**
     * 이력 버전 식별자
     */
    private Long profileVersionId;

    /**
     * LV1
     */
    private Integer age;
    private Long incomeYear;
    private String gender;

    /**
     * LV2
     */
    private String employmentType;
    private String residenceType;

    /**
     * LV3
     */
    private String loanPurpose;
    private Long totalDebt;
    private Integer existingLoanCount;

    /**
     * 메타 정보
     */
    private LocalDateTime createdAt;
    private boolean judgable;

    protected UserProfile() {
        // MyBatis 매핑 전용
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getProfileVersionId() {
        return profileVersionId;
    }

    public Integer getAge() {
        return age;
    }

    public Long getIncomeYear() {
        return incomeYear;
    }

    public String getGender() {
        return gender;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public String getResidenceType() {
        return residenceType;
    }

    public String getLoanPurpose() {
        return loanPurpose;
    }

    public Long getTotalDebt() {
        return totalDebt;
    }

    public Integer getExistingLoanCount() {
        return existingLoanCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isJudgable() {
        return judgable;
    }
}
