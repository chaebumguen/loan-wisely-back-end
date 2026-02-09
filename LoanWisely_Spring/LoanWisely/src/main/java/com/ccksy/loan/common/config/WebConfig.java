package com.ccksy.loan.common.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins:http://localhost:*}")
    private String allowedOrigins;

    /**
     * (Version 1) CORS 설정은 환경별로 달라질 수 있으므로
     * 최소 기본값 + application.yml로 전환 가능하도록 구성합니다.
     *
     * - 운영에서는 allowedOriginPatterns를 좁게 설정하는 것을 권장합니다.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        registry.addMapping("/**")
                // 개발 기본값: 로컬 허용. 운영에서는 제한 권장
                .allowedOriginPatterns(origins.length == 0 ? new String[] {"http://localhost:*"} : origins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Location", "Content-Disposition")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * swagger/정적 리소스가 추가될 경우 예외 경로를 추가하기 위한 자리입니다.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // no-op (Version 1)
    }
}
