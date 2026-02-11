package com.ccksy.loan.domain.admin.audit.mapper;

import com.ccksy.loan.domain.admin.audit.entity.AuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AuditLogMapper {
    int insert(AuditLog log);

    Long selectNextId();

    List<AuditLog> selectAll();
}
