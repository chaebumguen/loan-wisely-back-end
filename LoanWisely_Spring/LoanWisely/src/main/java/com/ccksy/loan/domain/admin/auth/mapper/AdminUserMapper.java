package com.ccksy.loan.domain.admin.auth.mapper;

import com.ccksy.loan.domain.admin.auth.entity.AdminUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminUserMapper {
    AdminUser selectByUsername(@Param("username") String username);

    List<String> selectRolesByAdminId(@Param("adminId") Long adminId);
}
