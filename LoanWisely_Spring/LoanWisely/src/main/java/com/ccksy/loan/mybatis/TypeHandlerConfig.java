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
 * TypeHandlerConfig (v1) - FIX (MyBatisConfig? ?뺥빀)
 *
 * ?뺥빀 湲곗?:
 * - MapperScan? common/config/MyBatisConfig ?먯꽌留?愿由ы븳??
 * - 蹂??대옒?ㅻ뒈 TypeHandler ?깅줉留??섑뻾?쒕떎.
 * - @MapperScan ?덈? 湲덉? (service/strategy ?깆쓣 mapper濡??ㅼ씤 ?깅줉?섎뒈 ?μ븷 ?щ컻 諛⑹?)
 */
@org.springframework.context.annotation.Configuration
public class TypeHandlerConfig {

    /**
     * MyBatis ConfigurationCustomizer
     * - SqlSessionFactory瑜?吏곸젒 ?앹꽦/??뼱?곗? ?딄퀬,
     * - MyBatis Configuration??TypeHandler留??깅줉?쒕떎.
     */
    @Bean
    public ConfigurationCustomizer typeHandlerCustomizer(
            ObjectMapper objectMapper
    ) {
        return (Configuration cfg) -> {
            TypeHandlerRegistry r = cfg.getTypeHandlerRegistry();

            // ?듯빀 TypeHandler ?깅줉(?⑥씪 ?뚯씪, ??낅퀎 ?대? ?대옒??
            r.register(Boolean.class, new UnifiedTypeHandler.BooleanYN());
            r.register(boolean.class, new UnifiedTypeHandler.BooleanYN());

            r.register(Map.class, new UnifiedTypeHandler.JsonMap(objectMapper));
            r.register(List.class, new UnifiedTypeHandler.StringListCsv());
        };
    }

    /**
     * ?⑥씪 ?뚯씪 ??TypeHandler 紐⑥쓬(v1)
     * - javaType蹂?handler瑜?遺꾨━?섏뿬 紐⑦샇???쒓굅
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
         * Map<String,Object> <-> JSON 臾몄옄??
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
         * List<String> <-> CSV 臾몄옄??
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
