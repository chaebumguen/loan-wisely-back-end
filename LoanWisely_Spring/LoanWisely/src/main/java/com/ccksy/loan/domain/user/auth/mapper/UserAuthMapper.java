package com.ccksy.loan.domain.user.auth.mapper;

import com.ccksy.loan.domain.user.auth.entity.UserAuth;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserAuthMapper {
    UserAuth selectByUsername(@Param("username") String username);

    UserAuth selectByUserId(@Param("userId") Long userId);

    Long selectNextId();

    int insertUserAuth(UserAuth userAuth);

    int incrementFailLoginCount(@Param("userId") Long userId);

    int resetFailLoginCount(@Param("userId") Long userId);

    int lockUser(@Param("userId") Long userId);

    int updateLastLoginAt(@Param("userId") Long userId);
}
