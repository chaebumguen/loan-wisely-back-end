package com.ccksy.loan.domain.product.controller;

import com.ccksy.loan.domain.product.dto.request.LoanProductRequest;
import com.ccksy.loan.domain.product.dto.response.LoanProductResponse;
import com.ccksy.loan.domain.product.service.LoanProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(LoanProductController.BASE_PATH)
public class LoanProductController {

    private static final Logger log = LoggerFactory.getLogger(LoanProductController.class);

    /**
     * 변동 후보(확인 필요):
     * - 실제 외부 노출 path는 API 명세서에서 “상품 조회” 행이 본 캡처 구간에 없어서 확인이 안 됩니다.
     * - 우선 v1 기본값으로 /api/products 를 잡아두고, 확정 시 BASE_PATH만 수정하면 됩니다.
     */
    public static final String BASE_PATH = "/api/products";

    private final LoanProductService service;

    public LoanProductController(LoanProductService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(LoanProductRequest req) {
        int total = service.countProducts(req);
        List<LoanProductResponse> items = service.listProducts(req);

        Map<String, Object> body = new HashMap<>();
        body.put("page", req.getPage());
        body.put("size", req.getSize());
        body.put("total", total);
        body.put("items", items);

        // 호출 후 “사용 자원” 짧게 출력
        log.info("RESOURCE_USED: controller=LoanProductController action=list path={} spec={}", BASE_PATH, "SPEC-2026-01-29-v1");

        return ResponseEntity.ok(body);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> detail(@PathVariable long productId) {
        LoanProductResponse item = service.getProductDetail(productId);

        Map<String, Object> body = new HashMap<>();
        body.put("item", item);

        log.info("RESOURCE_USED: controller=LoanProductController action=detail path={}/{} spec={}", BASE_PATH, productId, "SPEC-2026-01-29-v1");

        return ResponseEntity.ok(body);
    }
}
