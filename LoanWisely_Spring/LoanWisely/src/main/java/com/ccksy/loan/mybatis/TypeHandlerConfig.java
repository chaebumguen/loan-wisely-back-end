package com.ccksy.loan.mybatis;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration as SpringConfiguration;

/**
 * MyBatis TypeHandler 설정
 */
@SpringConfiguration
public class TypeHandlerConfig {

    @Bean
    public ConfigurationCustomizer typeHandlerCustomizer() {
        return new ConfigurationCustomizer() {
            @Override
            public void customize(Configuration configuration) {
                TypeHandlerRegistry registry = configuration.getTypeHandlerRegistry();
                // EnumTypeHandler는 XML 또는 @MappedTypes로 개별 Enum에 바인딩
            }
        };
    }
}
