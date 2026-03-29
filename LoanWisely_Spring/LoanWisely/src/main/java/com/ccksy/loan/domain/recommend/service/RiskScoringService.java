package com.ccksy.loan.domain.recommend.service;

import com.ccksy.loan.domain.recommend.service.model.RiskScoreResult;

public interface RiskScoringService {
    RiskScoreResult score(Long userId, Integer requestedInputLevel);
}
