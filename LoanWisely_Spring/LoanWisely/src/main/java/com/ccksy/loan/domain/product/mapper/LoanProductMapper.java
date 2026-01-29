package com.ccksy.loan.domain.product.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ccksy.loan.domain.product.dto.response.LoanProductResponse;

/**
 * 대출 상품 조회 MyBatis Mapper
 *
 * 책임:
 * - 대출 상품 목록 조회 SQL 호출
 * - 대출 상품 단건 조회 SQL 호출
 * - 전체 상품 건수 조회
 *
 * 주의:
 * - 비즈니스 로직 절대 금지
 * - 단순 SQL 호출 전용
 */
@Mapper
public interface LoanProductMapper {

    /**
     * 대출 상품 목록 조회 (페이징)
     *
     * @param offset 조회 시작 위치
     * @param size   조회 개수
     */
    List<LoanProductResponse> selectProducts(
            @Param("offset") int offset,
            @Param("size") int size
    );

    /**
     * 대출 상품 전체 건수 조회
     */
    long countProducts();

    /**
     * 대출 상품 단건 조회
     *
     * @param productId 상품 ID
     */
    LoanProductResponse selectProductById(
            @Param("productId") Long productId
    );
}
