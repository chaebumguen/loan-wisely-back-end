package com.ccksy.loan.domain.admin.rawfile.mapper;

import com.ccksy.loan.domain.admin.rawfile.entity.RawFileNormalized;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RawFileNormalizedMapper {
    int insertBatch(@Param("rows") List<RawFileNormalized> rows);

    int deleteByUploadId(@Param("uploadId") Long uploadId);

    int countByUploadId(@Param("uploadId") Long uploadId);

    List<RawFileNormalized> selectByUploadId(@Param("uploadId") Long uploadId);
}
