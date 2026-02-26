package com.ccksy.loan.domain.admin.rawfile.mapper;

import com.ccksy.loan.domain.admin.rawfile.entity.EdaOutlierResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EdaOutlierResultMapper {
    int insertBatch(@Param("rows") List<EdaOutlierResult> rows);

    Long selectNextId();
}
