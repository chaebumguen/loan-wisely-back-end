package com.ccksy.loan.mybatis;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;

/**
 * Enum <-> VARCHAR 공통 핸들러
 *
 * 주의:
 * - 이 핸들러는 생성자에 Enum 클래스 타입이 필요하므로,
 *   "그대로 자동 등록"만 해두면 실사용이 애매해질 수 있습니다.
 * - v1에서는 필요 Enum부터 "Enum별 register" 방식으로 쓰는 것을 권장합니다.
 */
public class EnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

    private final Class<E> type;

    public EnumTypeHandler(Class<E> type) {
        if (type == null) throw new IllegalArgumentException("Enum type must not be null");
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toEnum(rs.getString(columnName));
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toEnum(rs.getString(columnIndex));
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toEnum(cs.getString(columnIndex));
    }

    private E toEnum(String value) throws SQLException {
        if (value == null) return null;
        try {
            return Enum.valueOf(type, value);
        } catch (IllegalArgumentException ex) {
            throw new SQLException("Unknown enum value '" + value + "' for enum type " + type.getName(), ex);
        }
    }
}
