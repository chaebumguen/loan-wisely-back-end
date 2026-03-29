package com.ccksy.loan.domain.user.mapper;

import com.ccksy.loan.domain.user.entity.UserCreditLv1;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserCreditLv1Mapper {

    int insert(UserCreditLv1 entity);

    int deactivateActiveByUserId(@Param("userId") Long userId);

    UserCreditLv1 selectLatestActiveByUserId(@Param("userId") Long userId);

    java.util.List<UserCreditLv1> selectHistoryByUserId(@Param("userId") Long userId);

    Long selectNextId();
}
