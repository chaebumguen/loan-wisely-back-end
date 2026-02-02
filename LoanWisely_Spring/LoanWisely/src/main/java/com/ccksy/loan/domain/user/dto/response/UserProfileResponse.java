package com.ccksy.loan.domain.user.dto.response;

import java.time.LocalDateTime;

public class UserProfileResponse {

    /**
     * 프로필 버전 식별자 (이력 기반)
     */
    private final Long profileVersionId;

    /**
     * LV1 정보
     */
    private final Integer age;
    private final Long incomeYear;
    private final String gender;

    /**
     * LV2 정보
     */
    private final String employmentType;
    private final String residenceType;

    /**
     * LV3 정보
     */
    private final String loanPurpose;
    private final Long totalDebt;
    private final Integer existingLoanCount;

    /**
     * 메타 정보
     */
    private final LocalDateTime createdAt;
    private final boolean judgable;

    public UserProfileResponse(
            Long profileVersionId,
            Integer age,
