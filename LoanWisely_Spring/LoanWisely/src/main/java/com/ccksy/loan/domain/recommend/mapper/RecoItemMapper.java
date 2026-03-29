package com.ccksy.loan.domain.recommend.mapper;

import com.ccksy.loan.domain.recommend.entity.RecoItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecoItemMapper {

    int insert(RecoItem item);

    Long selectCurrentId();

    RecoItem selectById(@Param("itemId") Long itemId);

    List<RecoItem> selectByResultId(@Param("resultId") Long resultId);
}
