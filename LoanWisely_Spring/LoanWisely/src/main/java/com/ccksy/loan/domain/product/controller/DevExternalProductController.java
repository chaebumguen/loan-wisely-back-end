package com.ccksy.loan.domain.product.controller;

import com.ccksy.loan.common.response.ApiResponse;
import com.ccksy.loan.domain.product.client.ExternalLoanProductDto;
import com.ccksy.loan.domain.product.service.ExternalProductFetchService;
import com.ccksy.loan.domain.product.service.ExternalProductSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/dev/external-products")
@RequiredArgsConstructor
public class DevExternalProductController {

    private final ExternalProductFetchService externalProductFetchService;
    private final ExternalProductSyncService externalProductSyncService;

    @GetMapping
    public ApiResponse<List<ExternalLoanProductDto>> fetchExternalProducts() {
        log.info("Dev external products endpoint called");
        List<ExternalLoanProductDto> products = externalProductFetchService.fetchExternalProducts();
        log.info("Dev external products fetched. count={}", products.size());
        return ApiResponse.ok(products);
    }

    @GetMapping("/mortgage")
    public ApiResponse<List<ExternalLoanProductDto>> fetchMortgageProducts() {
        List<ExternalLoanProductDto> products = externalProductFetchService.fetchMortgageProducts();
        return ApiResponse.ok(products);
    }

    @GetMapping("/rent")
    public ApiResponse<List<ExternalLoanProductDto>> fetchRentProducts() {
        List<ExternalLoanProductDto> products = externalProductFetchService.fetchRentProducts();
        return ApiResponse.ok(products);
    }

    @GetMapping("/sync")
    public ApiResponse<Integer> syncExternalProducts() {
        log.info("Dev external products sync called");
        int count = externalProductSyncService.syncCreditProducts();
        return ApiResponse.ok(count);
    }

    @GetMapping("/sync-mortgage")
    public ApiResponse<Integer> syncMortgageProducts() {
        int count = externalProductSyncService.syncMortgageProducts();
        return ApiResponse.ok(count);
    }

    @GetMapping("/sync-rent")
    public ApiResponse<Integer> syncRentProducts() {
        int count = externalProductSyncService.syncRentProducts();
        return ApiResponse.ok(count);
    }

    @GetMapping("/sync-all")
    public ApiResponse<Integer> syncAllProducts() {
        int count = externalProductSyncService.syncAll();
        return ApiResponse.ok(count);
    }
}
