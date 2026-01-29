package com.ccksy.loan.domain.product.controller;

import com.ccksy.loan.common.response.ApiResponse;
import com.ccksy.loan.common.response.PageResponse;
import com.ccksy.loan.domain.product.dto.response.LoanProductResponse;
import com.ccksy.loan.domain.product.service.LoanProductService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 대출 상품 조회 Controller (Facade)
 *
 * 책임:
 * - HTTP 요청 수신
 * - Service 호출
 * - 공통 응답(ApiResponse)으로 감싸서 반환
 *
 * 원칙:
 * - 비즈니스 로직 없음
 * - 페이징 계산 없음
 * - 예외 처리 없음 (GlobalExceptionHandler 위임)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class LoanProductController {

    private final LoanProductService loanProductService;

    /**
     * 대출 상품 목록 조회 (페이징)
     *
     * 예시:
     * GET /api/products?page=1&size=10
     */
    @GetMapping
    public ApiResponse<PageResponse<LoanProductResponse>> getProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponse<LoanProductResponse> result =
                loanProductService.getProducts(page, size);

        return ApiResponse.success(result);
    }

    /**
     * 대출 상품 단건 조회
     *
     * 예시:
     * GET /api/products/1001
     */
    @GetMapping("/{productId}")
    public ApiResponse<LoanProductResponse> getProduct(
            @PathVariable Long productId) {

        LoanProductResponse result =
                loanProductService.getProductById(productId);

        return ApiResponse.success(result);
    }
}
