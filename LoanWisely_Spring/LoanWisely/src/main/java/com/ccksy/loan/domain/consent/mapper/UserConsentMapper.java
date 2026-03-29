package com.ccksy.loan.domain.consent.mapper;

import com.ccksy.loan.domain.consent.entity.UserConsent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserConsentMapper {

    int insertUserConsent(UserConsent userConsent);

    int deactivateActiveByUserIdAndLevel(@Param("userId") Long userId,
                                         @Param("consentLevel") Integer consentLevel);

    UserConsent selectLatestActiveByUserIdAndLevel(@Param("userId") Long userId,
                                                   @Param("consentLevel") Integer consentLevel);

    List<UserConsent> selectActiveByUserId(@Param("userId") Long userId);

    List<UserConsent> selectHistoryByUserIdAndLevel(@Param("userId") Long userId,
                                                    @Param("consentLevel") Integer consentLevel);
}
