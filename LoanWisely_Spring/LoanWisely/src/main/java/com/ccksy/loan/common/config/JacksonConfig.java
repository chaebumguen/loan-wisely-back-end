package com.ccksy.loan.common.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    /**
     * 결정론/재현성:
     * - 날짜/시간 직렬화는 ISO-8601 문자열로 고정
     * - null 필드는 응답에서 제외
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // deprecated API 대신 최신 방식
        mapper.setDefaultPropertyInclusion(
            JsonInclude.Value.construct(
                JsonInclude.Include.NON_NULL,
                JsonInclude.Include.NON_NULL
            )
        );

        // 외부/내부 스키마 버전 차이 내성
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // 숫자 결정론(BigDecimal 유지)
        mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);

        return mapper;
    }
}
