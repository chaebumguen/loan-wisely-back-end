package com.ccksy.loan.domain.product.service;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import com.ccksy.loan.domain.product.entity.Provider;
import com.ccksy.loan.domain.product.mapper.ProviderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProviderService {

    private final ProviderMapper providerMapper;

    @Transactional
    public Provider getOrCreate(String finCoNo, String companyName) {
        if (finCoNo == null || finCoNo.isBlank()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "finCoNo is required.");
        }

        Provider existing = providerMapper.selectByFinCoNo(finCoNo);
        if (existing != null) {
            if (companyName != null && !companyName.isBlank()
                    && !companyName.equals(existing.getCompanyName())) {
                providerMapper.updateName(existing.getProviderId(), companyName);
            }
            return existing;
        }

        LocalDateTime now = LocalDateTime.now();
        Provider provider = Provider.builder()
                .finCoNo(finCoNo)
                .companyName(companyName == null ? "" : companyName)
                .createdAt(now)
                .updatedAt(now)
                .build();
        int inserted = providerMapper.insert(provider);
        if (inserted != 1) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Provider insert failed.");
        }
        Provider saved = providerMapper.selectByFinCoNo(finCoNo);
        if (saved == null) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Provider select failed after insert.");
        }
        return saved;
    }
}
