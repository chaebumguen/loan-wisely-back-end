// FILE: domain/user/dto/response/UserProfileResponse.java
package com.ccksy.loan.domain.user.dto.response;

import java.math.BigDecimal;

/**
 * 사용자 프로필 응답 DTO (v1)
 *
 * 역할:
 * - 사용자 프로필 정보를 외부(UI/API)에 전달하기 위한 Response 전용 객체
 * - 판단/정책/추천 로직 절대 포함 금지
 *
 * LV 정책:
 * - LV1 / LV2 / LV3 필드는 모두 "운반"만 수행
 * - 노출/마스킹/동의 검증은 상위 계층 책임
 */
public final class UserProfileResponse {

    // ---------- 식별자 ----------
    private Long userId;

    // ---------- LV1 (기본 정보) ----------
    private Integer age;
    private BigDecimal annualIncome;
    private String gender;

    // ---------- LV2 (선택 정보) ----------
    private String employmentType;
    private String residenceType;

    // ---------- LV3 (금융 정보) ----------
    private String loanPurpose;          // 대출 목적
    private BigDecimal totalDebtAmount;  // 총 부채
    private Integer existingLoanCount;   // 기존 대출 건수

    public UserProfileResponse() {
        // 기본 생성자 (Jackson / 직렬화 대응)
    }

    // ---------- getter / setter ----------

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // LV1
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public BigDecimal getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(BigDecimal annualIncome) {
        this.annualIncome = annualIncome;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = normalize(gender);
    }

    // LV2
    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = normalize(employmentType);
    }

    public String getResidenceType() {
        return residenceType;
    }

    public void setResidenceType(String residenceType) {
        this.residenceType = normalize(residenceType);
    }

    // LV3
    public String getLoanPurpose() {
        return loanPurpose;
    }

    public void setLoanPurpose(String loanPurpose) {
        this.loanPurpose = normalize(loanPurpose);
    }

    public BigDecimal getTotalDebtAmount() {
        return totalDebtAmount;
    }

    public void setTotalDebtAmount(BigDecimal totalDebtAmount) {
        this.totalDebtAmount = totalDebtAmount;
    }

    public Integer getExistingLoanCount() {
        return existingLoanCount;
    }

    public void setExistingLoanCount(Integer existingLoanCount) {
        this.existingLoanCount = existingLoanCount;
    }

    // ---------- util ----------
    private String normalize(String v) {
        if (v == null) return null;
        String s = v.trim();
        return s.isEmpty() ? null : s;
    }
}
