package com.ccksy.loan.ml;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * ML Server HTTP Client
 *
 * - 외부 ML 서버와의 통신 전용
 * - 비즈니스/도메인 로직은 포함하지 않음
 */
@Component
public class MlServerHttpClient {

    private final RestTemplate restTemplate;

    public MlServerHttpClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * ML 서버 원천 응답 조회
     *
     * @param endpointUrl ML 서버 API 엔드포인트
     * @return 원천 응답 문자열(가공 금지)
     */
    public String fetchRawResponse(String endpointUrl) {
        return restTemplate.getForObject(endpointUrl, String.class);
    }
}
