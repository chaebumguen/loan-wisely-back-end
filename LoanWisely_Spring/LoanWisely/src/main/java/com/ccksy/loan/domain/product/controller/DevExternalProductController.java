package com.ccksy.loan.domain.product.controller;

import com.ccksy.loan.common.response.ApiResponse;
import com.ccksy.loan.domain.product.client.ExternalLoanProductDto;
import com.ccksy.loan.domain.product.service.ExternalProductFetchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Profile("dev")
@RestController
@RequestMapping("/api/dev/external-products")
@RequiredArgsConstructor
public class DevExternalProductController {

    private final ExternalProductFetchService externalProductFetchService;

    @GetMapping
    public ApiResponse<List<ExternalLoanProductDto>> fetchExternalProducts() {
        log.info("Dev external products endpoint called");
        List<ExternalLoanProductDto> products = externalProductFetchService.fetchExternalProducts();
        log.info("Dev external products fetched. count={}", products.size());
        return ApiResponse.ok(products);
    }
}
