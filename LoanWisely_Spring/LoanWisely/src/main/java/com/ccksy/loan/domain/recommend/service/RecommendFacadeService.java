// FILE: domain/recommend/service/RecommendFacadeService.java
package com.ccksy.loan.domain.recommend.service;

import com.ccksy.loan.domain.recommend.dto.request.RecommendRequest;
import com.ccksy.loan.domain.recommend.result.response.RecommendResponse;

/**
 * Facade Service 계약 (v1)
 * - Controller는 이 인터페이스만 의존한다.
 */
public interface RecommendFacadeService {

    RecommendResponse recommend(RecommendRequest request);

    Object explain(Long recommendationId);
}
