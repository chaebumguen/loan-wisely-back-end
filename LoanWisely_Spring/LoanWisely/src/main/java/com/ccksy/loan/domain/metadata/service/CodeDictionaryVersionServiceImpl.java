package com.ccksy.loan.domain.metadata.service;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import com.ccksy.loan.domain.metadata.entity.CodeDictionaryVersion;
import com.ccksy.loan.domain.metadata.mapper.CodeDictionaryVersionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 메타데이터 버전 조회 서비스
 * - 승인/활성 플래그 기반 조회
 */
@Service
@RequiredArgsConstructor
public class CodeDictionaryVersionServiceImpl implements CodeDictionaryVersionService {

    private final CodeDictionaryVersionMapper versionMapper;

    @Override
    @Transactional(readOnly = true)
    public CodeDictionaryVersion getActiveVersion() {
        CodeDictionaryVersion active = versionMapper.selectActive();
        if (active == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "활성 메타데이터 버전이 존재하지 않습니다.");
        }
        return active;
    }
}
