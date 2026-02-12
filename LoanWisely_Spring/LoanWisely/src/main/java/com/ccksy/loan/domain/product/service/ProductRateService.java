package com.ccksy.loan.domain.product.service;

import com.ccksy.loan.domain.product.entity.ProductInterestRate;
import com.ccksy.loan.domain.product.entity.ProductRateSnapshot;
import com.ccksy.loan.domain.product.mapper.ProductInterestRateMapper;
import com.ccksy.loan.domain.product.mapper.ProductRateSnapshotMapper;
import com.ccksy.loan.domain.user.entity.UserCreditLv1;
import com.ccksy.loan.domain.user.mapper.UserCreditLv1Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class ProductRateService {

    private static final BigDecimal LIMIT_MIN = new BigDecimal("5000000");
    private static final BigDecimal LIMIT_MAX = new BigDecimal("300000000");
    private static final BigDecimal ROUND_UNIT = new BigDecimal("10000");

    private final ProductRateSnapshotMapper productRateSnapshotMapper;
    private final ProductInterestRateMapper productInterestRateMapper;
    private final UserCreditLv1Mapper userCreditLv1Mapper;

    public ProductRateQuote getRateQuote(Long productId) {
        if (productId == null) {
            return null;
        }
        ProductRateSnapshot snapshot = productRateSnapshotMapper.selectLatestByProductId(productId);
        if (snapshot != null) {
            return ProductRateQuote.builder()
                    .productId(productId)
                    .rateMin(snapshot.getRateMin())
                    .rateMax(snapshot.getRateMax())
                    .scoreBase(snapshot.getScoreBase())
                    .rateType(snapshot.getRateType())
                    .asOfDate(snapshot.getAsOfDate())
                    .build();
        }
        ProductInterestRate interestRate = productInterestRateMapper.selectLatestByProductId(productId);
        if (interestRate == null) {
            return null;
        }
        return ProductRateQuote.builder()
                .productId(productId)
                .rateMin(interestRate.getRateMin())
                .rateMax(interestRate.getRateMax())
                .scoreBase(null)
                .rateType(null)
                .asOfDate(interestRate.getAsOfDate())
                .build();
    }

    public BigDecimal estimateLimit(Long userId, ProductRateQuote quote) {
        if (userId == null) {
            return null;
        }
        UserCreditLv1 lv1 = userCreditLv1Mapper.selectLatestActiveByUserId(userId);
        if (lv1 == null) {
            return null;
        }
        return estimateLimit(lv1, quote);
    }

    public BigDecimal estimateLimit(UserCreditLv1 lv1, ProductRateQuote quote) {
        if (lv1 == null || lv1.getIncomeYear() == null) {
            return null;
        }

        BigDecimal income = new BigDecimal(lv1.getIncomeYear());
        BigDecimal incomeFactor = new BigDecimal("0.45");
        BigDecimal ageFactor = ageFactor(lv1.getAge());
        BigDecimal ratePenalty = ratePenalty(quote);
        BigDecimal rateMinPenalty = rateMinPenalty(quote);
        BigDecimal productRateFactor = productRateFactor(quote);

        BigDecimal limit = income.multiply(incomeFactor)
                .multiply(ageFactor)
                .multiply(ratePenalty)
                .multiply(rateMinPenalty)
                .multiply(productRateFactor);
        if (limit.compareTo(LIMIT_MIN) < 0) {
            limit = LIMIT_MIN;
        }
        if (limit.compareTo(LIMIT_MAX) > 0) {
            limit = LIMIT_MAX;
        }
        return limit.divide(ROUND_UNIT, 0, RoundingMode.DOWN).multiply(ROUND_UNIT);
    }

    private BigDecimal ageFactor(Integer age) {
        if (age == null) return new BigDecimal("0.8");
        if (age < 25) return new BigDecimal("0.6");
        if (age < 35) return new BigDecimal("0.8");
        if (age < 50) return BigDecimal.ONE;
        if (age < 60) return new BigDecimal("0.9");
        return new BigDecimal("0.7");
    }

    private BigDecimal ratePenalty(ProductRateQuote quote) {
        if (quote == null || quote.getRateMax() == null) {
            return BigDecimal.ONE;
        }
        if (quote.getRateMax().compareTo(new BigDecimal("20")) >= 0) {
            return new BigDecimal("0.85");
        }
        if (quote.getRateMax().compareTo(new BigDecimal("12")) >= 0) {
            return new BigDecimal("0.9");
        }
        return BigDecimal.ONE;
    }

    private BigDecimal rateMinPenalty(ProductRateQuote quote) {
        if (quote == null || quote.getRateMin() == null) {
            return BigDecimal.ONE;
        }
        BigDecimal rateMin = quote.getRateMin();
        if (rateMin.compareTo(new BigDecimal("10")) >= 0) {
            return new BigDecimal("0.92");
        }
        if (rateMin.compareTo(new BigDecimal("7")) >= 0) {
            return new BigDecimal("0.96");
        }
        if (rateMin.compareTo(new BigDecimal("5")) >= 0) {
            return new BigDecimal("0.98");
        }
        return BigDecimal.ONE;
    }

    private BigDecimal productRateFactor(ProductRateQuote quote) {
        if (quote == null) {
            return BigDecimal.ONE;
        }
        BigDecimal rateMin = quote.getRateMin();
        BigDecimal rateMax = quote.getRateMax();
        BigDecimal rateAvg = null;
        if (rateMin != null && rateMax != null) {
            rateAvg = rateMin.add(rateMax).divide(new BigDecimal("2"), 4, RoundingMode.HALF_UP);
        } else if (rateMin != null) {
            rateAvg = rateMin;
        } else if (rateMax != null) {
            rateAvg = rateMax;
        }

        BigDecimal avgFactor = BigDecimal.ONE;
        if (rateAvg != null) {
            avgFactor = BigDecimal.ONE.subtract(rateAvg.multiply(new BigDecimal("0.005")));
            avgFactor = clamp(avgFactor, new BigDecimal("0.85"), new BigDecimal("1.05"));
        }

        BigDecimal spanFactor = BigDecimal.ONE;
        if (rateMin != null && rateMax != null) {
            BigDecimal span = rateMax.subtract(rateMin).abs();
            spanFactor = BigDecimal.ONE.subtract(span.multiply(new BigDecimal("0.01")));
            spanFactor = clamp(spanFactor, new BigDecimal("0.90"), BigDecimal.ONE);
        }

        BigDecimal typeFactor = BigDecimal.ONE;
        if (quote.getRateType() != null) {
            String type = quote.getRateType().toUpperCase();
            if (type.contains("변동") || type.equals("C")) {
                typeFactor = new BigDecimal("0.97");
            } else if (type.contains("고정") || type.equals("F")) {
                typeFactor = BigDecimal.ONE;
            } else {
                typeFactor = new BigDecimal("0.99");
            }
        }

        BigDecimal scoreFactor = BigDecimal.ONE;
        if (quote.getScoreBase() != null) {
            BigDecimal base = quote.getScoreBase();
            scoreFactor = BigDecimal.ONE.add(base.divide(new BigDecimal("1000"), 4, RoundingMode.HALF_UP));
            scoreFactor = clamp(scoreFactor, new BigDecimal("0.90"), new BigDecimal("1.05"));
        }

        return avgFactor.multiply(spanFactor).multiply(typeFactor).multiply(scoreFactor);
    }

    private BigDecimal clamp(BigDecimal value, BigDecimal min, BigDecimal max) {
        if (value.compareTo(min) < 0) {
            return min;
        }
        if (value.compareTo(max) > 0) {
            return max;
        }
        return value;
    }
}
