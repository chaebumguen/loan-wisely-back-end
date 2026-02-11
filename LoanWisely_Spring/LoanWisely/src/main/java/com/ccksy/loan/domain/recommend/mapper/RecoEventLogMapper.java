package com.ccksy.loan.domain.recommend.mapper;

import com.ccksy.loan.domain.recommend.entity.RecoEventLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecoEventLogMapper {

    int insert(RecoEventLog log);

    List<RecoEventLog> selectByProductId(@Param("productId") Long productId);
}
