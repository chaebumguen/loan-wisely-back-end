package com.ccksy.loan.domain.user.mapper;

import com.ccksy.loan.domain.user.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserProfileMapper {

    /**
     * 최신 유효 프로필 조회 (판단 시점 기준)
     */
    UserProfile selectLatestValidByUserId(@Param("userId") Long userId);

    /**
     * 특정 버전 프로필 조회 (감사/재현용)
     */
    UserProfile selectByUserIdAndVersion(
            @Param("userId") Long userId,
            @Param("profileVersionId") Long profileVersionId
    );

    /**
     * 프로필 이력 적재 (append-only)
     */
    int insert(UserProfile userProfile);

    /**
     * 사용자 전체 이력 조회 (관리/감사용)
     */
    List<UserProfile> selectHistoryByUserId(@Param("userId") Long userId);
}
