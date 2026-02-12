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
            return externalProductClient.fetchCreditProducts();
        } catch (Exception e) {
            log.warn("?몃? ?곹뭹 API ?몄텧 ?ㅽ뙣", e);
            return Collections.emptyList();
        }
    }

    public List<ExternalLoanProductDto> fetchMortgageProducts() {
        try {
            return externalProductClient.fetchMortgageProducts();
        } catch (Exception e) {
            log.warn("External product API(mortgage) failed", e);
            return Collections.emptyList();
        }
    }

    public List<ExternalLoanProductDto> fetchRentProducts() {
        try {
            return externalProductClient.fetchRentProducts();
        } catch (Exception e) {
            log.warn("External product API(rent) failed", e);
            return Collections.emptyList();
        }
    }
}


