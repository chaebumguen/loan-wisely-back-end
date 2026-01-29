package com.ccksy.loan.domain.product.service;

import com.ccksy.loan.common.response.PageResponse;
import com.ccksy.loan.domain.product.dto.response.LoanProductResponse;

/**
 * 대출 상품 조회 Service
 *
 * 책임:
 * - 대출 상품 목록 조회 (페이징)
 * - 대출 상품 단건 조회
 *
 * 설계 의도:
 * - 로그인/인증, 사용자 식별과 완전히 분리된 조회 전용 서비스
 * - 데이터 소스(DB, 외부 API, Django 전처리 서버 등)에 의존하지 않는 계약
 * - 추천/자격 판단 로직은 Recommend 도메인에서 처리
 *
 * 확장 고려:
 * - 현재는 DB 기반 조회로 구현 가능
 * - 추후 금감원 API → Django 1차 필터링 → Spring 연동 시
 *   ServiceImpl 내부 구현만 교체/확장
 */
public interface LoanProductService {

    /**
     * 대출 상품 목록 조회 (페이징)
     *
     * @param page 페이지 번호 (1-based)
     * @param size 페이지 크기
     * @return 대출 상품 페이징 응답
     */
    PageResponse<LoanProductResponse> getProducts(int page, int size);

    /**
     * 대출 상품 단건 조회
     *
     * @param productId 상품 ID
     * @return 대출 상품 상세 정보
     */
    LoanProductResponse getProductById(Long productId);
}
