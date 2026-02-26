package com.ccksy.loan.domain.user.mapper;

import com.ccksy.loan.domain.user.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserProfileMapper {

    int insertUserProfile(UserProfile userProfile);

    int deactivateActiveByUserId(@Param("userId") Long userId);

    UserProfile selectLatestActiveByUserId(@Param("userId") Long userId);

    List<UserProfile> selectHistoryByUserId(@Param("userId") Long userId);
}
