package com.ccksy.loan.domain.recommend.service;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import com.ccksy.loan.domain.recommend.entity.RecoPolicy;
import com.ccksy.loan.domain.recommend.mapper.RecoPolicyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 정책 버전 조회 서비스
 * - 승인/활성 플래그 기반 조회
 */
@Service
@RequiredArgsConstructor
public class RecoPolicyServiceImpl implements RecoPolicyService {

    private final RecoPolicyMapper policyMapper;

    @Override
    @Transactional(readOnly = true)
    public RecoPolicy getActivePolicy() {
        RecoPolicy active = policyMapper.selectActive();
        if (active == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "활성 정책 버전이 존재하지 않습니다.");
        }
        return active;
    }
}
