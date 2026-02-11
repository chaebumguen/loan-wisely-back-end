package com.ccksy.loan.domain.recommend.service;

import com.ccksy.loan.domain.recommend.dto.request.RecommendRequest;
import com.ccksy.loan.domain.recommend.result.response.RecommendResponse;

public interface RecommendFacadeService {

    RecommendResponse recommend(RecommendRequest request);

    RecommendResponse reproduce(String reproduceKey);
}
