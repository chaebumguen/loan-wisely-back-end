package com.ccksy.loan.domain.recommend.mapper;

import com.ccksy.loan.domain.recommend.entity.RecoExclusionReason;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecoExclusionReasonMapper {

    int insert(RecoExclusionReason reason);

    List<RecoExclusionReason> selectByResultId(@Param("resultId") Long resultId);
}
