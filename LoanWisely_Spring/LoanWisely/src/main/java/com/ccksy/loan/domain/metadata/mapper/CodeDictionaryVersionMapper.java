package com.ccksy.loan.domain.metadata.mapper;

import com.ccksy.loan.domain.metadata.entity.CodeDictionaryVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CodeDictionaryVersionMapper {

    int insert(CodeDictionaryVersion version);

    Long selectNextId();

    CodeDictionaryVersion selectById(@Param("versionId") Long versionId);

    CodeDictionaryVersion selectLatest();

    CodeDictionaryVersion selectActive();

    java.util.List<CodeDictionaryVersion> selectAll();

    int updateStatus(@Param("versionId") Long versionId,
                     @Param("status") String status,
                     @Param("approvedBy") String approvedBy,
                     @Param("approvedAt") java.time.LocalDateTime approvedAt,
                     @Param("isActive") String isActive);

    int deactivateAll();
}
