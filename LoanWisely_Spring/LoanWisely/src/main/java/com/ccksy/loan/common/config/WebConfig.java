package com.ccksy.loan.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * (Version 1) CORS 정책은 환경별로 달라질 수 있으므로,
     * 최소 안전 기본값 + application.yml로 치환 가능하도록 구성합니다.
     *
     * - 운영에서는 allowedOriginPatterns를 좁히는 것을 권장합니다.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 개발 기본값: 로컬 프론트 연동을 고려 (운영 시 제한 권장)
                .allowedOriginPatterns("http://localhost:*", "https://localhost:*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Location", "Content-Disposition")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * swagger/정적 리소스가 도입될 경우 예외 경로를 추가하기 위한 자리입니다.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // no-op (Version 1)
    }
}
