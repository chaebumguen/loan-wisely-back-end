package com.ccksy.loan.ml;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * MI Server HTTP Client
 *
 * - 외부 MI 서버와의 통신 전용
 * - 비즈니스/도메인/판단 로직 절대 포함 금지
 */
@Component
public class MlServerHttpClient {

    private final RestTemplate restTemplate;

    public MlServerHttpClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * MI 서버 원천 응답 조회
     *
     * @param endpointUrl MI 서버 API 엔드포인트
     * @return 원천 응답 문자열 (가공 금지)
     */
    public String fetchRawResponse(String endpointUrl) {
        return restTemplate.getForObject(endpointUrl, String.class);
    }
}
