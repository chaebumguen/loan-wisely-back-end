package com.ccksy.loan.domain.product.service;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import com.ccksy.loan.domain.product.client.ExternalLoanProductDto;
import com.ccksy.loan.domain.product.entity.LoanProduct;
import com.ccksy.loan.domain.product.entity.ProductInterestRate;
import com.ccksy.loan.domain.product.entity.Provider;
import com.ccksy.loan.domain.product.mapper.LoanProductMapper;
import com.ccksy.loan.domain.product.mapper.ProductInterestRateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalProductSyncService {

    private final ExternalProductFetchService externalProductFetchService;
    private final ProviderService providerService;
    private final LoanProductMapper loanProductMapper;
    private final ProductInterestRateMapper productInterestRateMapper;

    @Transactional
    public int syncCreditProducts() {
        List<ExternalLoanProductDto> products = externalProductFetchService.fetchExternalProducts();
        int updated = 0;
        for (ExternalLoanProductDto dto : products) {
            updated += upsertProduct(dto) ? 1 : 0;
        }
        log.info("External credit products sync done. count={}", updated);
        return updated;
    }

    @Transactional
    public int syncMortgageProducts() {
        List<ExternalLoanProductDto> products = externalProductFetchService.fetchMortgageProducts();
        int updated = 0;
        for (ExternalLoanProductDto dto : products) {
            updated += upsertProduct(dto) ? 1 : 0;
        }
        log.info("External mortgage products sync done. count={}", updated);
        return updated;
    }

    @Transactional
    public int syncRentProducts() {
        List<ExternalLoanProductDto> products = externalProductFetchService.fetchRentProducts();
        int updated = 0;
        for (ExternalLoanProductDto dto : products) {
            updated += upsertProduct(dto) ? 1 : 0;
        }
        log.info("External rent products sync done. count={}", updated);
        return updated;
    }

    @Transactional
    public int syncAll() {
        int total = 0;
        total += syncCreditProducts();
        total += syncMortgageProducts();
        total += syncRentProducts();
        return total;
    }

    private boolean upsertProduct(ExternalLoanProductDto dto) {
        if (dto == null || dto.getFinCoNo() == null || dto.getFinPrdtCd() == null) {
            return false;
        }
        Provider provider = providerService.getOrCreate(dto.getFinCoNo(), dto.getCompanyName());
        LoanProduct existing = loanProductMapper.selectByExternalKey(dto.getFinCoNo(), dto.getFinPrdtCd());

        LoanProduct entity = LoanProduct.builder()
                .productId(existing == null ? null : existing.getProductId())
                .providerId(provider.getProviderId())
                .productName(dto.getProductName())
                .productTypeCodeValueId(dto.getProductTypeCodeValueId())
                .loanTypeCodeValueId(dto.getLoanTypeCodeValueId())
                .repaymentTypeCodeValueId(dto.getRepaymentTypeCodeValueId())
                .collateralTypeCodeValueId(dto.getCollateralTypeCodeValueId())
                .rateTypeCodeValueId(dto.getRateTypeCodeValueId())
                .note(dto.getNote())
                .finPrdtCd(dto.getFinPrdtCd())
                .finCoNo(dto.getFinCoNo())
                .companyName(dto.getCompanyName())
                .joinWay(dto.getJoinWay())
                .cbName(dto.getCbName())
                .endDate(dto.getEndDate())
                .updatedAt(LocalDateTime.now())
                .build();

        if (existing == null) {
            entity = entity.toBuilder().addDate(LocalDateTime.now()).build();
            int inserted = loanProductMapper.insert(entity);
            if (inserted != 1) {
                throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Loan product insert failed.");
            }
            existing = loanProductMapper.selectByExternalKey(dto.getFinCoNo(), dto.getFinPrdtCd());
            if (existing == null) {
                throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Loan product select failed after insert.");
            }
        } else {
            int u = loanProductMapper.update(entity);
            if (u != 1) {
                throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Loan product update failed.");
            }
        }

        if (existing != null && (dto.getRateMin() != null || dto.getRateMax() != null || dto.getRateBase() != null)) {
            ProductInterestRate rate = ProductInterestRate.builder()
                    .productId(existing.getProductId())
                    .rateMin(dto.getRateMin())
                    .rateMax(dto.getRateMax())
                    .rateBase(dto.getRateBase())
                    .asOfDate(dto.getAsOfDate() == null ? LocalDate.now() : dto.getAsOfDate())
                    .createdAt(LocalDateTime.now())
                    .build();
            productInterestRateMapper.insert(rate);
        }
        return true;
    }
}
