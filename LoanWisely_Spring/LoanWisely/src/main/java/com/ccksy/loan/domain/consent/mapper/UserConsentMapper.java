package com.ccksy.loan.domain.consent.mapper;

import com.ccksy.loan.domain.consent.entity.UserConsent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserConsentMapper {

    int insert(UserConsent consent);

    List<UserConsent> findActiveByUserId(@Param("userId") Long userId);
}
