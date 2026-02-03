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
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * MyBatisConfig (v1) - FIX
 *
 * 紐⑹쟻:
 * - MapperScan 踰붿쐞瑜?mapper ?⑦궎吏濡쒕쭔 ?쒗븳 (service/strategy ?ㅼ씤 ?ㅼ틪 諛⑹?)
 * - SqlSessionFactory / SqlSessionTemplate ??紐낆떆?곸쑝濡??쒓났?섏뿬
 *   "Property 'sqlSessionFactory' or 'sqlSessionTemplate' are required" ?ㅻ쪟瑜?諛⑹?
 *
 * ?꾩젣:
 * - DataSource??諛섎뱶??議댁옱?댁빞 ??(DB ?곌껐 ?ㅼ젙 ?꾩슂)
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
     * SqlSessionFactory 紐낆떆 ?쒓났
     * - MyBatis-SpringBoot-Starter ?먮룞援ъ꽦???숈옉?섏? ?딄굅??
     *   而ㅼ뒪? 援ъ꽦?쇰줈 ?명빐 factory/template???꾨씫?섎뒈 ?곹솴??李⑤떒
     *
     * - ConfigurationCustomizer(?? TypeHandlerConfig?먯꽌 ?깅줉??TypeHandler ??瑜??곸슜
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(
            DataSource dataSource,
            ObjectProvider<ConfigurationCustomizer> customizers
    ) throws Exception {

        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(
                new PathMatchingResourcePatternResolver()
                        .getResources("classpath*:mybatis/mapper/**/*.xml")
        );
        factoryBean.setTypeAliasesPackage("com.ccksy.loan.domain");

        // 而ㅼ뒪?곕쭏?댁? ?곸슜(??낇빖?ㅻ윭, ?ㅼ젙 ??
        org.apache.ibatis.session.Configuration mybatisCfg = new org.apache.ibatis.session.Configuration();
        mybatisCfg.setMapUnderscoreToCamelCase(true);
        customizers.orderedStream().forEach(c -> c.customize(mybatisCfg));
        factoryBean.setConfiguration(mybatisCfg);

        SqlSessionFactory factory = factoryBean.getObject();
        if (factory == null) {
            throw new IllegalStateException("Failed to create SqlSessionFactory (factoryBean.getObject() returned null).");
        }
        return factory;
    }

    /**
     * SqlSessionTemplate 紐낆떆 ?쒓났
     * - MapperFactoryBean??二쇱엯諛쏅뒈 ?듭떖 ???
     */
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}