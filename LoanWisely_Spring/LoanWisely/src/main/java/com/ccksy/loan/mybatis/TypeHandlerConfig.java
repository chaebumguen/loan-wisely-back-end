package com.ccksy.loan.mybatis;

import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class TypeHandlerConfig {

    @Bean
    public ConfigurationCustomizer typeHandlerCustomizer() {
        return new ConfigurationCustomizer() {
            @Override
            public void customize(Configuration configuration) {
                // 필요 시 TypeHandler 등록
            }
        };
    }
}
