package com.ccksy.loan.ml;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * MI Server HTTP Client
 *
 * - ?몃? MI ?쒕쾭????듭떊 ?꾩슜
 * - 鍮꾩쫰?덉뒪/?꾨찓???먮떒 濡쒖쭅 ?덈? ?ы븿 湲덉?
 */
@Component
public class MlServerHttpClient {

    private final RestTemplate restTemplate;

    public MlServerHttpClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * MI ?쒕쾭 ?먯쿇 ?묐떟 議고쉶
     *
     * @param endpointUrl MI ?쒕쾭 API ?붾뱶?ъ씤??
     * @return ?먯쿇 ?묐떟 臾몄옄??(媛怨?湲덉?)
     */
    public String fetchRawResponse(String endpointUrl) {
        return restTemplate.getForObject(endpointUrl, String.class);
    }
}