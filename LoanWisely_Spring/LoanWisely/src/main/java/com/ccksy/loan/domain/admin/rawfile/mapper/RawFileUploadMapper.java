package com.ccksy.loan.domain.admin.rawfile.mapper;

import com.ccksy.loan.domain.admin.rawfile.entity.RawFileUpload;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RawFileUploadMapper {

    int insert(RawFileUpload upload);

    Long selectNextId();

    RawFileUpload selectById(@Param("uploadId") Long uploadId);

    List<RawFileUpload> selectAll();

    int updateStatus(@Param("uploadId") Long uploadId,
                     @Param("status") String status);
}
