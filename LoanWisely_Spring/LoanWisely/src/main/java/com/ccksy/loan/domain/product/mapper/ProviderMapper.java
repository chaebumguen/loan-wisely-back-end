package com.ccksy.loan.domain.product.mapper;

import com.ccksy.loan.domain.product.entity.Provider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProviderMapper {
    Provider selectByFinCoNo(@Param("finCoNo") String finCoNo);

    int insert(Provider provider);

    int updateName(@Param("providerId") Long providerId, @Param("companyName") String companyName);
}
