package com.ccksy.loan.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.ccksy.loan.domain", annotationClass = org.apache.ibatis.annotations.Mapper.class)
public class MyBatisConfig {
    // mybatis-spring-boot-starter 사용 시 기본 패키지 스캔 설정
    // v1에서는 @MapperScan 방식으로 통일
}
