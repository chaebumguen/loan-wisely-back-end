package com.ccksy.loan.domain.recommend.service;

import com.ccksy.loan.domain.recommend.filter.model.FilterContext;

public interface FeatureService {
    void buildFeatures(FilterContext ctx, String metaVersion);
}
