package com.ccksy.loan.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.ccksy.loan.domain")
public class MyBatisConfig {
    /**
     * (Version 1)
     * - MyBatis-SpringBoot-Starter를 사용한다는 전제에서, MapperScan만 고정합니다.
     * - SqlSessionFactory, TypeHandler 등은 필요 시 infrastructure/mybatis 쪽과 연동하여 확장합니다.
     */
}
