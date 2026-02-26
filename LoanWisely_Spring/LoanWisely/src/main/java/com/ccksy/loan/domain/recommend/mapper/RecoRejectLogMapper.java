package com.ccksy.loan.domain.recommend.mapper;

import com.ccksy.loan.domain.recommend.entity.RecoRejectLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecoRejectLogMapper {

    int insert(RecoRejectLog log);

    List<RecoRejectLog> selectByRequestId(@Param("requestId") Long requestId);
}
