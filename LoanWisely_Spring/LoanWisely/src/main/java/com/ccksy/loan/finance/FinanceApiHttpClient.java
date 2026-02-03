package com.ccksy.loan.finance;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * ?몃? 湲덉쑖 API HTTP ?몄텧 ?꾩슜 Client
 * - 湲곗닠???듭떊 梨낆엫留?蹂댁쑀
 * - 鍮꾩쫰?덉뒪/?꾨찓???댁꽍 ?덈? 湲덉?
 */
@Component
public class FinanceApiHttpClient {

    private final RestTemplate restTemplate;

    public FinanceApiHttpClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * ?몃? 湲덉쑖 ?곹뭹 ?먯쿇 ?곗씠??議고쉶
     *
     * @param endpointUrl ?몃? API ?붾뱶?ъ씤??
     * @return ?먯쿇 ?묐떟 臾몄옄??(媛怨?湲덉?)
     */
    public String fetchRawProductData(String endpointUrl) {
        return restTemplate.getForObject(endpointUrl, String.class);
    }
}