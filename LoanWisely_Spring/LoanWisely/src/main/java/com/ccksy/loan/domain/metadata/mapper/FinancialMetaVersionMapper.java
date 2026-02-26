package com.ccksy.loan.domain.metadata.mapper;

import com.ccksy.loan.domain.metadata.entity.FinancialMetaVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface FinancialMetaVersionMapper {
    int insert(FinancialMetaVersion version);

    Long selectNextId();

    FinancialMetaVersion selectById(@Param("versionId") Long versionId);

    FinancialMetaVersion selectActive();

    List<FinancialMetaVersion> selectAll();

    int updateStatus(@Param("versionId") Long versionId,
                     @Param("status") String status,
                     @Param("approvedBy") String approvedBy,
                     @Param("approvedAt") LocalDateTime approvedAt,
                     @Param("isActive") String isActive);

    int deactivateAll();
}
