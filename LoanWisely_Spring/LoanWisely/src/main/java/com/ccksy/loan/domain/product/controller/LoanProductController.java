package com.ccksy.loan.domain.product.controller;

import com.ccksy.loan.common.response.ApiResponse;
import com.ccksy.loan.domain.product.dto.request.LoanProductRequest;
import com.ccksy.loan.domain.product.dto.response.LoanProductResponse;
import com.ccksy.loan.domain.product.service.LoanProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class LoanProductController {

    private final LoanProductService loanProductService;

    /**
     * 상품 단건 조회
     */
    @GetMapping("/{productId}")
    public ApiResponse<LoanProductResponse> getById(@PathVariable Long productId) {
        return ApiResponse.ok(loanProductService.getById(productId));
    }

    /**
     * 상품 목록 조회(간단 검색)
     * - 실제 필터/정렬/추천 로직은 domain/recommend에서 수행
     */
    @GetMapping
    public ApiResponse<List<LoanProductResponse>> search(
            @RequestParam(required = false) Long providerId,
            @RequestParam(required = false) String productTypeCodeValueId,
            @RequestParam(required = false) String loanTypeCodeValueId,
            @RequestParam(required = false) String repaymentTypeCodeValueId
    ) {
        return ApiResponse.ok(
                loanProductService.search(providerId, productTypeCodeValueId, loanTypeCodeValueId, repaymentTypeCodeValueId)
        );
    }

    /**
     * (내부관리) 상품 등록/수정
     * - v1에서는 권한/인증은 common/security 영역에서 통제된다는 전제(여기서는 엔드포인트만 고정)
     */
    @PostMapping
    public ApiResponse<LoanProductResponse> upsert(@Valid @RequestBody LoanProductRequest request) {
        return ApiResponse.ok(loanProductService.upsert(request));
    }
}
