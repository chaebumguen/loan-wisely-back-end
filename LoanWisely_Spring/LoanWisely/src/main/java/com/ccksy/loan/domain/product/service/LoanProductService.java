package com.ccksy.loan.domain.product.service;

import com.ccksy.loan.domain.product.dto.request.LoanProductRequest;
import com.ccksy.loan.domain.product.dto.response.LoanProductResponse;
import com.ccksy.loan.domain.product.entity.LoanProduct;

import java.util.List;

public interface LoanProductService {

    List<LoanProductResponse> listProducts(LoanProductRequest req);

    int countProducts(LoanProductRequest req);

    LoanProductResponse getProductDetail(long productId);

    // 내부관리(적재 파이프라인) 용도: 외부 원천 → 정규테이블 적재 결과를 upsert
    void upsertProduct(LoanProduct product);
}
