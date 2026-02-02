package com.ccksy.loan.proxy.ml;

import com.ccksy.loan.ml.MIServerHttpClient;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MI Server Cached Proxy
 *
 * - MIServerProxy 확장
 * - 캐시 책임만 추가
 */
@Component
public class CachedMIServerProxy extends MIServerProxy {

    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public CachedMIServerProxy(MIServerHttpClient httpClient) {
        super(httpClient);
    }

    @Override
    public String fetchRawResponse(String endpointUrl) {
        return cache.computeIfAbsent(
                endpointUrl,
                key -> super.fetchRawResponse(key)
        );
    }
}
