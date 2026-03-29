package com.ccksy.loan.domain.admin.rawfile.mapper;

import com.ccksy.loan.domain.admin.rawfile.entity.EdaStatResult;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EdaStatResultMapper {
    Long selectNextId();

    int insert(EdaStatResult result);
}
