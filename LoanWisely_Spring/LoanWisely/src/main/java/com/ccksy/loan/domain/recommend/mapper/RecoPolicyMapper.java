package com.ccksy.loan.domain.recommend.mapper;

import com.ccksy.loan.domain.recommend.entity.RecoPolicy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecoPolicyMapper {

    int insert(RecoPolicy policy);

    Long selectNextId();

    RecoPolicy selectById(@Param("policyId") Long policyId);

    List<RecoPolicy> selectByVersion(@Param("version") String version);

    RecoPolicy selectLatest();

    RecoPolicy selectActive();

    List<RecoPolicy> selectAll();

    int updatePolicyValue(@Param("policyId") Long policyId,
                          @Param("policyValue") String policyValue);

    int approvePolicy(@Param("policyId") Long policyId,
                      @Param("status") String status,
                      @Param("approvedBy") String approvedBy,
                      @Param("approvedAt") java.time.LocalDateTime approvedAt);

    int deactivateAll();

    int activatePolicy(@Param("policyId") Long policyId);
}
