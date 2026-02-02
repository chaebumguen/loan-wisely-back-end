// FILE: mybatis/TypeHandlerConfig.java
package com.ccksy.loan.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;

/**
 * TypeHandlerConfig (v1) - FIX (MyBatisConfig와 정합)
 *
 * 정합 기준:
 * - MapperScan은 common/config/MyBatisConfig 에서만 관리한다.
 * - 본 클래스는 TypeHandler 등록만 수행한다.
 * - @MapperScan 절대 금지 (service/strategy 등을 mapper로 오인 등록하는 장애 재발 방지)
 */
@org.springframework.context.annotation.Configuration
public class TypeHandlerConfig {

    /**
     * MyBatis용 ObjectMapper
     * - 프로젝트 공통 ObjectMapper(@Bean)가 이미 존재하면 이 Bean은 제거하고 주입으로만 받도록 조정 가능
     */
    @Bean
    public ObjectMapper mybatisObjectMapper() {
        return new ObjectMapper();
    }

    /**
     * MyBatis ConfigurationCustomizer
     * - SqlSessionFactory를 직접 생성/덮어쓰지 않고,
     * - MyBatis Configuration에 TypeHandler만 등록한다.
     */
    @Bean
    public ConfigurationCustomizer typeHandlerCustomizer(
            ObjectMapper mybatisObjectMapper,
            EnumTypeHandler enumTypeHandler // 프로젝트에 이미 존재(Bean)
    ) {
        return (Configuration cfg) -> {
            TypeHandlerRegistry r = cfg.getTypeHandlerRegistry();

            // 1) EnumTypeHandler 등록 (기존 구현)
            r.register(enumTypeHandler);

            // 2) 통합 TypeHandler 등록(단일 파일, 타입별 내부 클래스)
            r.register(Boolean.class, new UnifiedTypeHandler.BooleanYN());
            r.register(boolean.class, new UnifiedTypeHandler.BooleanYN());

            r.register(Map.class, new UnifiedTypeHandler.JsonMap(mybatisObjectMapper));
            r.register(List.class, new UnifiedTypeHandler.StringListCsv());
        };
    }

    /**
     * 단일 파일 내 TypeHandler 모음(v1)
     * - javaType별 handler를 분리하여 모호성 제거
     */
    static final class UnifiedTypeHandler {

        private UnifiedTypeHandler() {}

        /**
         * Boolean <-> 'Y'/'N'
         */
        static final class BooleanYN extends BaseTypeHandler<Boolean> {

            @Override
            public void setNonNullParameter(
                    PreparedStatement ps,
                    int i,
                    Boolean parameter,
                    JdbcType jdbcType
            ) throws SQLException {
                ps.setString(i, Boolean.TRUE.equals(parameter) ? "Y" : "N");
            }

            @Override
            public Boolean getNullableResult(ResultSet rs, String columnName) throws SQLException {
                return parse(rs.getString(columnName));
            }

            @Override
            public Boolean getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
                return parse(rs.getString(columnIndex));
            }

            @Override
            public Boolean getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
                return parse(cs.getString(columnIndex));
            }

            private Boolean parse(String v) {
                if (v == null) return null;
                String s = v.trim();
                if (s.isEmpty()) return null;

                if ("Y".equalsIgnoreCase(s) || "1".equals(s) || "TRUE".equalsIgnoreCase(s)) return Boolean.TRUE;
                if ("N".equalsIgnoreCase(s) || "0".equals(s) || "FALSE".equalsIgnoreCase(s)) return Boolean.FALSE;

                throw new IllegalArgumentException("Invalid Y/N boolean value: " + v);
            }
        }

        /**
         * Map<String,Object> <-> JSON 문자열
         */
        static final class JsonMap extends BaseTypeHandler<Map<String, Object>> {

            private static final TypeReference<Map<String, Object>> MAP_REF = new TypeReference<>() {};
            private final ObjectMapper om;

            JsonMap(ObjectMapper om) {
                this.om = om;
            }

            @Override
            public void setNonNullParameter(
                    PreparedStatement ps,
                    int i,
                    Map<String, Object> parameter,
                    JdbcType jdbcType
            ) throws SQLException {
                try {
                    ps.setString(i, om.writeValueAsString(parameter));
                } catch (Exception e) {
                    throw new SQLException("Failed to serialize Map to JSON", e);
                }
            }

            @Override
            public Map<String, Object> getNullableResult(ResultSet rs, String columnName) throws SQLException {
                return parse(rs.getString(columnName));
            }

            @Override
            public Map<String, Object> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
                return parse(rs.getString(columnIndex));
            }

            @Override
            public Map<String, Object> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
                return parse(cs.getString(columnIndex));
            }

            private Map<String, Object> parse(String v) throws SQLException {
                if (v == null) return null;
                String s = v.trim();
                if (s.isEmpty()) return null;

                try {
                    return om.readValue(s, MAP_REF);
                } catch (Exception e) {
                    throw new SQLException("Failed to deserialize JSON to Map", e);
                }
            }
        }

        /**
         * List<String> <-> CSV 문자열
         */
        static final class StringListCsv extends BaseTypeHandler<List<String>> {

            @Override
            public void setNonNullParameter(
                    PreparedStatement ps,
                    int i,
                    List<String> parameter,
                    JdbcType jdbcType
            ) throws SQLException {
                if (parameter == null || parameter.isEmpty()) {
                    ps.setString(i, "");
                    return;
                }
                ps.setString(i, String.join(",", parameter));
            }

            @Override
            public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
                return parse(rs.getString(columnName));
            }

            @Override
            public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
                return parse(rs.getString(columnIndex));
            }

            @Override
            public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
                return parse(cs.getString(columnIndex));
            }

            private List<String> parse(String v) {
                if (v == null) return null;

                String s = v.trim();
                if (s.isEmpty()) return Collections.emptyList();

                List<String> out = Arrays.stream(s.split((","))
                        ).map(String::trim)
                        .filter(t -> !t.isEmpty())
                        .toList();

                return out;
            }
        }
    }
}
