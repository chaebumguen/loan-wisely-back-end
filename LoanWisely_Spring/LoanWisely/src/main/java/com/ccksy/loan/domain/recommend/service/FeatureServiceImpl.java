package com.ccksy.loan.domain.recommend.service;

import com.ccksy.loan.domain.recommend.filter.model.FilterContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class FeatureServiceImpl implements FeatureService {

    @Override
    public void buildFeatures(FilterContext ctx, String metaVersion) {
        if (ctx == null) return;

        Integer creditScore = ctx.getCreditScore();
        BigDecimal dsr = ctx.getDsr();
        String purpose = ctx.getLoanPurposeCode();
        Integer inputLv = ctx.getInputLv();

        ctx.putAttr("featureVersion", metaVersion);
        ctx.putAttr("feature.creditScore", creditScore);
        ctx.putAttr("feature.creditScoreBucket", creditScoreBucket(creditScore));
        ctx.putAttr("feature.dsr", dsr);
        ctx.putAttr("feature.dsrBucket", dsrBucket(dsr));
        ctx.putAttr("feature.loanPurpose", purpose);
        ctx.putAttr("feature.inputLevel", inputLv);
    }

    private Integer creditScoreBucket(Integer score) {
        if (score == null) return null;
        if (score < 500) return 1;
        if (score < 650) return 2;
        if (score < 750) return 3;
        if (score < 850) return 4;
        return 5;
    }

    private Integer dsrBucket(BigDecimal dsr) {
        if (dsr == null) return null;
        if (dsr.compareTo(new BigDecimal("0.2")) <= 0) return 1;
        if (dsr.compareTo(new BigDecimal("0.4")) <= 0) return 2;
        if (dsr.compareTo(new BigDecimal("0.6")) <= 0) return 3;
        return 4;
    }
}
