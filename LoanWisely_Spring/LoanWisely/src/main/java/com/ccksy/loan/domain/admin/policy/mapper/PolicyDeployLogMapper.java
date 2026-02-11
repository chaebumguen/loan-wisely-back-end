package com.ccksy.loan.domain.admin.policy.mapper;

import com.ccksy.loan.domain.admin.policy.entity.PolicyDeployLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PolicyDeployLogMapper {
    Long selectNextId();

    int insert(PolicyDeployLog log);

    PolicyDeployLog selectLatestByPolicyId(@Param("policyId") Long policyId);

    List<PolicyDeployLog> selectByPolicyId(@Param("policyId") Long policyId);
}
