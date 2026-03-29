package com.ccksy.loan.domain.user.mapper;

import com.ccksy.loan.domain.user.entity.UserCreditLv2;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserCreditLv2Mapper {

    int insert(UserCreditLv2 entity);

    int deactivateActiveByUserId(@Param("userId") Long userId);

    UserCreditLv2 selectLatestActiveByUserId(@Param("userId") Long userId);

    java.util.List<UserCreditLv2> selectHistoryByUserId(@Param("userId") Long userId);

    Long selectNextId();
}
