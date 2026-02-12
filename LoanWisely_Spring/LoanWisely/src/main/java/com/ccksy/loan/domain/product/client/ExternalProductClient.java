package com.ccksy.loan.domain.product.client;

import java.util.List;

public interface ExternalProductClient {
    List<ExternalLoanProductDto> fetchCreditProducts();

    List<ExternalLoanProductDto> fetchMortgageProducts();

    List<ExternalLoanProductDto> fetchRentProducts();
}
