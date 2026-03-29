package com.ccksy.loan.domain.recommend.service;

import com.ccksy.loan.domain.recommend.service.model.RiskScoreResult;
import com.ccksy.loan.domain.user.entity.UserCreditLv1;
import com.ccksy.loan.domain.user.entity.UserCreditLv2;
import com.ccksy.loan.domain.user.entity.UserCreditLv3;
import com.ccksy.loan.domain.user.mapper.UserCreditLv1Mapper;
import com.ccksy.loan.domain.user.mapper.UserCreditLv2Mapper;
import com.ccksy.loan.domain.user.mapper.UserCreditLv3Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class RiskScoringServiceImpl implements RiskScoringService {

    private static final int SCORE_MIN = 300;
    private static final int SCORE_MAX = 900;
    private static final int SCORE_BASE = 600;

    private final UserCreditLv1Mapper userCreditLv1Mapper;
    private final UserCreditLv2Mapper userCreditLv2Mapper;
    private final UserCreditLv3Mapper userCreditLv3Mapper;

    @Override
    @Transactional(readOnly = true)
    public RiskScoreResult score(Long userId, Integer requestedInputLevel) {
        UserCreditLv1 lv1 = userCreditLv1Mapper.selectLatestActiveByUserId(userId);
        UserCreditLv2 lv2 = userCreditLv2Mapper.selectLatestActiveByUserId(userId);
        UserCreditLv3 lv3 = userCreditLv3Mapper.selectLatestActiveByUserId(userId);

        if (lv1 == null && lv2 == null && lv3 == null) {
            return RiskScoreResult.builder()
                    .creditScore(null)
                    .dsr(null)
                    .loanPurposeCode(null)
                    .build();
        }

        Integer creditScore = calculateCreditScore(lv1, lv2, lv3);
        BigDecimal dsr = calculateDsr(lv1, lv3);
        String loanPurpose = lv3 != null ? lv3.getLoanPurpose() : null;

        return RiskScoreResult.builder()
                .creditScore(creditScore)
                .dsr(dsr)
                .loanPurposeCode(loanPurpose)
                .build();
    }

    private Integer calculateCreditScore(UserCreditLv1 lv1, UserCreditLv2 lv2, UserCreditLv3 lv3) {
        int score = SCORE_BASE;

        if (lv1 != null) {
            score += scoreByAge(lv1.getAge());
            score += scoreByIncome(lv1.getIncomeYear());
        }
        if (lv2 != null) {
            score += scoreByEmploymentType(lv2.getEmploymentType());
            score += scoreByResidenceType(lv2.getResidenceType());
        }
        if (lv3 != null) {
            score += scoreByLoanCount(lv3.getExistingLoanCount());
            score += scoreByDebtRatio(lv1 != null ? lv1.getIncomeYear() : null, lv3.getTotalDebt());
        }

        if (score < SCORE_MIN) score = SCORE_MIN;
        if (score > SCORE_MAX) score = SCORE_MAX;
        return score;
    }

    private BigDecimal calculateDsr(UserCreditLv1 lv1, UserCreditLv3 lv3) {
        if (lv1 == null || lv3 == null) return null;
        Long incomeYear = lv1.getIncomeYear();
        Long totalDebt = lv3.getTotalDebt();
        if (incomeYear == null || incomeYear <= 0) return null;
        if (totalDebt == null || totalDebt < 0) return null;

        return new BigDecimal(totalDebt)
                .divide(new BigDecimal(incomeYear), 6, RoundingMode.HALF_UP);
    }

    private int scoreByAge(Integer age) {
        if (age == null) return 0;
        if (age < 20) return -50;
        if (age < 30) return 0;
        if (age < 40) return 20;
        if (age < 50) return 30;
        if (age < 60) return 20;
        return -20;
    }

    private int scoreByIncome(Long incomeYear) {
        if (incomeYear == null) return 0;
        if (incomeYear < 20_000_000L) return -30;
        if (incomeYear < 40_000_000L) return 0;
        if (incomeYear < 60_000_000L) return 20;
        if (incomeYear < 100_000_000L) return 40;
        return 60;
    }

    private int scoreByEmploymentType(String employmentType) {
        if (employmentType == null) return 0;
        String v = employmentType.toUpperCase();
        if (v.contains("FULL") || v.contains("정규")) return 30;
        if (v.contains("CONTRACT") || v.contains("계약")) return 10;
        if (v.contains("SELF") || v.contains("자영")) return 5;
        if (v.contains("UNEMP") || v.contains("무직")) return -80;
        return 0;
    }

    private int scoreByResidenceType(String residenceType) {
        if (residenceType == null) return 0;
        String v = residenceType.toUpperCase();
        if (v.contains("OWN") || v.contains("자가")) return 20;
        if (v.contains("LEASE") || v.contains("전세")) return 10;
        if (v.contains("RENT") || v.contains("월세")) return 0;
        return 0;
    }

    private int scoreByLoanCount(Integer count) {
        if (count == null) return 0;
        if (count >= 5) return -80;
        if (count >= 3) return -50;
        if (count >= 1) return -20;
        return 0;
    }

    private int scoreByDebtRatio(Long incomeYear, Long totalDebt) {
        if (incomeYear == null || incomeYear <= 0) return 0;
        if (totalDebt == null || totalDebt < 0) return 0;

        BigDecimal ratio = new BigDecimal(totalDebt)
                .divide(new BigDecimal(incomeYear), 6, RoundingMode.HALF_UP);

        if (ratio.compareTo(new BigDecimal("1.0")) > 0) return -80;
        if (ratio.compareTo(new BigDecimal("0.7")) > 0) return -50;
        if (ratio.compareTo(new BigDecimal("0.4")) > 0) return -20;
        return 10;
    }
}
