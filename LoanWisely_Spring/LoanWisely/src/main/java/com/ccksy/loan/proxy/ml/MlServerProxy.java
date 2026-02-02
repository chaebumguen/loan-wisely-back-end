package com.ccksy.loan.proxy.ml;

import com.ccksy.loan.ml.MlServerHttpClient;
import org.springframework.stereotype.Component;

/**
 * MI Server Proxy (Base)
 *
 * - 외부 MI 서버 호출의 기본 Proxy
 * - 기술적 보조만 수행
 */
@Component
public class MlServerProxy {

    protected final MlServerHttpClient httpClient;

    public MlServerProxy(MlServerHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * MI 서버 원천 응답 조회
     *
     * @param endpointUrl MI 서버 API 엔드포인트
     * @return 원천 응답 문자열
     */
    public String fetchRawResponse(String endpointUrl) {
        // TODO: 리트라이/타임아웃/서킷브레이커 훅 위치
        return httpClient.fetchRawResponse(endpointUrl);
    }
}
