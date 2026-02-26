package com.ccksy.loan.domain.recommend.mapper;

import com.ccksy.loan.domain.recommend.entity.RecoRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RecoRequestMapper {

    int insert(RecoRequest request);

    Long selectNextId();

    int insertWithId(RecoRequest request);

    RecoRequest selectById(@Param("requestId") Long requestId);
}
