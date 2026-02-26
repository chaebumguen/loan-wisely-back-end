package com.ccksy.loan.domain.admin.approval.mapper;

import com.ccksy.loan.domain.admin.approval.entity.ApprovalLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApprovalLogMapper {
    int insert(ApprovalLog log);

    Long selectNextId();
}
