// FILE: common/config/MyBatisConfig.java
package com.ccksy.loan.common.config;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatisConfig (v1) - FIX
 *
 * 핵심:
 * - MapperScan 범위를 domain 전체로 두면 service/strategy/state 같은 인터페이스까지
 *   MyBatis Mapper로 오인 등록되어 동일 타입 Bean이 2개 생기는 장애가 발생한다.
 *
 * 원칙:
 * - MapperScan은 mapper 패키지로만 제한한다.
 * - annotationClass=Mapper 로 "진짜 MyBatis Mapper(@Mapper)"만 스캔하도록 강제한다.
 */
@Configuration
@MapperScan(
        basePackages = {
                "com.ccksy.loan.domain.user.mapper",
                "com.ccksy.loan.domain.consent.mapper",
                "com.ccksy.loan.domain.product.mapper",
                "com.ccksy.loan.domain.recommend.mapper"
        },
        annotationClass = Mapper.class
)
public class MyBatisConfig {
    /**
     * (Version 1)
     * - MyBatis-SpringBoot-Starter 전제
     * - MapperScan만 고정
     * - SqlSessionFactory, TypeHandler 등은 별도 설정(예: mybatis/TypeHandlerConfig)에서 확장
     */
}
