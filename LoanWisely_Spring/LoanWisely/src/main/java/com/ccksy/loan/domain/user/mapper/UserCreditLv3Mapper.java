package com.ccksy.loan.domain.user.mapper;

import com.ccksy.loan.domain.user.entity.UserCreditLv3;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserCreditLv3Mapper {

    int insert(UserCreditLv3 entity);

    int deactivateActiveByUserId(@Param("userId") Long userId);

    UserCreditLv3 selectLatestActiveByUserId(@Param("userId") Long userId);

    java.util.List<UserCreditLv3> selectHistoryByUserId(@Param("userId") Long userId);

    Long selectNextId();
}
