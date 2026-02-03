package com.ccksy.loan.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpClientConfig {

    @Bean
    public RestTemplate restTemplate(
            @Value("${app.http-client.connect-timeout-ms:2000}") long connectTimeoutMs,
            @Value("${app.http-client.read-timeout-ms:5000}") long readTimeoutMs
    ) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) connectTimeoutMs);
        factory.setReadTimeout((int) readTimeoutMs);
        return new RestTemplate(factory);
    }
}