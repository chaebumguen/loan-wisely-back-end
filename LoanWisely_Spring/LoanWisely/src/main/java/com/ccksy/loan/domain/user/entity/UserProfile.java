package com.ccksy.loan.domain.user.entity;

import java.time.LocalDateTime;

/**
 * ?ъ슜???꾨줈???대젰 ?뷀떚??(Append-only)
 * ?먮떒/怨꾩궛/?곹깭 濡쒖쭅 ?놁쓬
 */
public class UserProfile {

    private Long id;
    private Long userId;

    /**
     * ?대젰 踰꾩쟾 ?앸퀎??
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
     * 硫뷀? ?뺣낫
     */
    private LocalDateTime createdAt;
    private boolean judgable;

    protected UserProfile() {
        // MyBatis 留ㅽ븨 ?꾩슜
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProfileVersionId() {
        return profileVersionId;
    }

    public void setProfileVersionId(Long profileVersionId) {
        this.profileVersionId = profileVersionId;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Long getIncomeYear() {
        return incomeYear;
    }

    public void setIncomeYear(Long incomeYear) {
        this.incomeYear = incomeYear;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public String getResidenceType() {
        return residenceType;
    }

    public void setResidenceType(String residenceType) {
        this.residenceType = residenceType;
    }

    public String getLoanPurpose() {
        return loanPurpose;
    }

    public void setLoanPurpose(String loanPurpose) {
        this.loanPurpose = loanPurpose;
    }

    public Long getTotalDebt() {
        return totalDebt;
    }

    public void setTotalDebt(Long totalDebt) {
        this.totalDebt = totalDebt;
    }

    public Integer getExistingLoanCount() {
        return existingLoanCount;
    }

    public void setExistingLoanCount(Integer existingLoanCount) {
        this.existingLoanCount = existingLoanCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isJudgable() {
        return judgable;
    }

    public void setJudgable(boolean judgable) {
        this.judgable = judgable;
    }
}