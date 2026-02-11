package com.ccksy.loan.domain.product.service;

import com.ccksy.loan.domain.product.client.ExternalLoanProductDto;
import com.ccksy.loan.domain.product.client.ExternalProductClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalProductFetchService {

    private final ExternalProductClient externalProductClient;

    public List<ExternalLoanProductDto> fetchExternalProducts() {
        try {
            return externalProductClient.fetchProducts();
        } catch (Exception e) {
            log.warn("외부 상품 API 호출 실패", e);
            return Collections.emptyList();
        }
    }
}
