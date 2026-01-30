package com.ccksy.loan.domain.product.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import com.ccksy.loan.common.exception.ValidationException;
import com.ccksy.loan.common.response.PageResponse;
import com.ccksy.loan.domain.product.dto.response.LoanProductResponse;
import com.ccksy.loan.domain.product.mapper.LoanProductMapper;

import lombok.RequiredArgsConstructor;

/**
 * 대출 상품 조회 Service 구현체
 *
 * 책임:
 * - 페이징 파라미터 검증
 * - page → offset 변환
 * - MyBatis Mapper 호출
 * - PageResponse 조립
 *
 * 설계 원칙:
 * - 데이터 출처(DB/외부 API)에 대한 판단은 이 레이어에서만 수행
 * - Controller는 비즈니스 로직을 알지 못한다
 */
@Service
@RequiredArgsConstructor
public class LoanProductServiceImpl implements LoanProductService {

    private final LoanProductMapper loanProductMapper;

    /**
     * 대출 상품 목록 조회 (페이징)
     */
    @Override
    public PageResponse<LoanProductResponse> getProducts(int page, int size) {

        // ===== 1. 파라미터 검증 =====
        if (page <= 0) {
            throw new ValidationException(
                    ErrorCode.VALIDATION_FAILED,
                    "페이지 번호는 1 이상이어야 합니다."
            );
        }

        if (size <= 0) {
            throw new ValidationException(
                    ErrorCode.VALIDATION_FAILED,
                    "페이지 크기는 1 이상이어야 합니다."
            );
        }

        // ===== 2. 페이징 계산 =====
        int offset = (page - 1) * size;

        // ===== 3. 전체 건수 조회 =====
        long totalCount = loanProductMapper.countProducts();

        if (totalCount == 0) {
            throw new BusinessException(
                    ErrorCode.PRODUCT_NOT_AVAILABLE,
                    "조회 가능한 대출 상품이 없습니다."
            );
        }

        // ===== 4. 목록 조회 =====
        List<LoanProductResponse> items =
                loanProductMapper.selectProducts(offset, size);

        // ===== 5. PageResponse 조립 =====
        return PageResponse.of(
                items,
                totalCount,
                page,
                size
        );
    }

    /**
     * 대출 상품 단건 조회
     */
    @Override
    public LoanProductResponse getProductById(Long productId) {

        // ===== 1. 파라미터 검증 =====
        Assert.notNull(productId, "productId must not be null");

        if (productId <= 0) {
            throw new ValidationException(
                    ErrorCode.VALIDATION_FAILED,
                    "상품 ID는 1 이상이어야 합니다."
            );
        }

        // ===== 2. 단건 조회 =====
        LoanProductResponse product =
                loanProductMapper.selectProductById(productId);

        if (product == null) {
            throw new BusinessException(
                    ErrorCode.PRODUCT_NOT_AVAILABLE,
                    "현재 이용 가능한 상품이 없습니다."
            );
        }

        return product;
    }
}
