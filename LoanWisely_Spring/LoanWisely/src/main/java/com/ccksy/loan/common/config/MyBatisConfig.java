// FILE: common/config/MyBatisConfig.java
package com.ccksy.loan.common.config;

import javax.sql.DataSource;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;

import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatisConfig (v1) - FIX
 *
 * 목적:
 * - MapperScan 범위를 mapper 패키지로만 제한 (service/strategy 오인 스캔 방지)
 * - SqlSessionFactory / SqlSessionTemplate 을 명시적으로 제공하여
 *   "Property 'sqlSessionFactory' or 'sqlSessionTemplate' are required" 오류를 방지
 *
 * 전제:
 * - DataSource는 반드시 존재해야 함 (DB 연결 설정 필요)
 */
//@Configuration
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
     * SqlSessionFactory 명시 제공
     * - MyBatis-SpringBoot-Starter 자동구성이 동작하지 않거나,
     *   커스텀 구성으로 인해 factory/template이 누락되는 상황을 차단
     *
     * - ConfigurationCustomizer(예: TypeHandlerConfig에서 등록한 TypeHandler 등)를 적용
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(
            DataSource dataSource,
            ObjectProvider<ConfigurationCustomizer> customizers
    ) throws Exception {

        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);

        // 커스터마이저 적용(타입핸들러, 설정 등)
        org.apache.ibatis.session.Configuration mybatisCfg = new org.apache.ibatis.session.Configuration();
        customizers.orderedStream().forEach(c -> c.customize(mybatisCfg));
        factoryBean.setConfiguration(mybatisCfg);

        SqlSessionFactory factory = factoryBean.getObject();
        if (factory == null) {
            throw new IllegalStateException("Failed to create SqlSessionFactory (factoryBean.getObject() returned null).");
        }
        return factory;
    }

    /**
     * SqlSessionTemplate 명시 제공
     * - MapperFactoryBean이 주입받는 핵심 대상
     */
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
