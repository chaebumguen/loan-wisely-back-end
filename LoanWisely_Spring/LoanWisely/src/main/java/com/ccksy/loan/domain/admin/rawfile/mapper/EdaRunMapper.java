package com.ccksy.loan.domain.admin.rawfile.mapper;

import com.ccksy.loan.domain.admin.rawfile.entity.EdaRun;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EdaRunMapper {
    Long selectNextId();

    int insert(EdaRun run);
}
