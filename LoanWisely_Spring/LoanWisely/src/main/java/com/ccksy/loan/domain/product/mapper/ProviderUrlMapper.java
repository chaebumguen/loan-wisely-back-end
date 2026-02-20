package com.ccksy.loan.domain.product.mapper;

import com.ccksy.loan.domain.product.entity.ProviderUrl;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProviderUrlMapper {
    ProviderUrl selectByFinCoNo(@Param("finCoNo") String finCoNo);

    int upsert(ProviderUrl providerUrl);
}
