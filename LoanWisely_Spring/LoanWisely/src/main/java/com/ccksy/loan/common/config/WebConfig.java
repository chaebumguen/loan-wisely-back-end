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
     * (Version 1) CORS ?뺤콉? ?섍꼍蹂꾨줈 ?щ씪吏????덉쑝誘濡?
     * 理쒖냼 ?덉쟾 湲곕낯媛?+ application.yml濡?移섑솚 媛?ν븯?꾨줉 援ъ꽦?⑸땲??
     *
     * - ?댁쁺?먯꽌??allowedOriginPatterns瑜?醫곹엳??寃껋쓣 沅뚯옣?⑸땲??
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        registry.addMapping("/**")
                // 媛쒕컻 湲곕낯媛? 濡쒖뺄 ?꾨줎???곕룞??怨좊젮 (?댁쁺 ???쒗븳 沅뚯옣)
                .allowedOriginPatterns(origins.length == 0 ? new String[] {"http://localhost:*"} : origins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Location", "Content-Disposition")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * swagger/?뺤쟻 由ъ냼?ㅺ? ?꾩엯??寃쎌슦 ?덉쇅 寃쎈줈瑜?異붽??섍린 ?꾪븳 ?먮━?낅땲??
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // no-op (Version 1)
    }
}