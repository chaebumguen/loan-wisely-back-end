package com.ccksy.loan.domain.admin.rawfile.mapper;

import com.ccksy.loan.domain.admin.rawfile.entity.RawFileCell;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RawFileCellMapper {
    int insertBatch(@Param("cells") List<RawFileCell> cells);

    List<RawFileCell> selectByUploadId(@Param("uploadId") Long uploadId);

    int deleteByUploadId(@Param("uploadId") Long uploadId);
}
