package com.ccksy.loan.domain.recommend.service;

import com.ccksy.loan.domain.recommend.dto.request.RecommendRequest;
import com.ccksy.loan.domain.recommend.result.response.RecommendResponse;

public interface RecommendFacadeService {

    /**
     * 추천 실행의 단일 진입점.
     * Controller는 본 인터페이스에만 의존한다.
     */
    RecommendResponse execute(RecommendRequest request);
}
