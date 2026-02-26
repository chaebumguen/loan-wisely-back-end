package com.ccksy.loan.domain.admin.rawfile.mapper;

import com.ccksy.loan.domain.admin.rawfile.entity.RawFileRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RawFileRowMapper {
    int insertBatch(@Param("rows") List<RawFileRow> rows);

    int insertOne(@Param("row") RawFileRow row);

    Long selectNextId();

    int countByUploadId(@Param("uploadId") Long uploadId);
}
