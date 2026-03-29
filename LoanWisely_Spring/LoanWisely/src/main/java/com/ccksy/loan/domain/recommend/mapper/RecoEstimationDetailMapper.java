package com.ccksy.loan.domain.recommend.mapper;

import com.ccksy.loan.domain.recommend.entity.RecoEstimationDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecoEstimationDetailMapper {

    int insert(RecoEstimationDetail detail);

    List<RecoEstimationDetail> selectByItemId(@Param("itemId") Long itemId);
}
