package com.ccksy.loan.finance;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 외부 금융 API HTTP 호출 전용 Client
 * - 기술적 통신 책임만 보유
 * - 비즈니스/도메인 해석 절대 금지
 */
@Component
public class FinanceApiHttpClient {

    private final RestTemplate restTemplate;

    public FinanceApiHttpClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 외부 금융 상품 원천 데이터 조회
     *
     * @param endpointUrl 외부 API 엔드포인트
     * @return 원천 응답 문자열 (가공 금지)
     */
    public String fetchRawProductData(String endpointUrl) {
        return restTemplate.getForObject(endpointUrl, String.class);
    }
}
