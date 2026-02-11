package com.ccksy.loan.domain.recommend.mapper;

import com.ccksy.loan.domain.recommend.entity.RecoResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RecoResultMapper {

    int insert(RecoResult result);

    Long selectNextId();

    int insertWithId(RecoResult result);

    RecoResult selectById(@Param("resultId") Long resultId);
}
