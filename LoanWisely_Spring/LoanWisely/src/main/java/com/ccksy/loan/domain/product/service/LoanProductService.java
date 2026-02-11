package com.ccksy.loan.domain.product.service;

import com.ccksy.loan.domain.product.dto.request.LoanProductRequest;
import com.ccksy.loan.domain.product.dto.response.LoanProductResponse;

import java.util.List;

public interface LoanProductService {

    LoanProductResponse getById(Long productId);

    List<LoanProductResponse> search(Long providerId,
                                    String productTypeCodeValueId,
                                    String loanTypeCodeValueId,
                                    String repaymentTypeCodeValueId);

    /**
     * productId가 있으면 update, 없으면 insert
     */
    LoanProductResponse upsert(LoanProductRequest request);
}
