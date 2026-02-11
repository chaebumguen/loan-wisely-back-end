package com.ccksy.loan.domain.product.client;

import java.util.List;

public interface ExternalProductClient {
    List<ExternalLoanProductDto> fetchProducts();
}
