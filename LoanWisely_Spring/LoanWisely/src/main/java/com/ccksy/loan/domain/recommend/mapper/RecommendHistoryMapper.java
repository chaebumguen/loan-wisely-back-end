package com.ccksy.loan.domain.recommend.mapper;

import com.ccksy.loan.domain.recommend.entity.RecommendHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecommendHistoryMapper {

    int insertRecommendHistory(RecommendHistory history);

    Long selectNextId();

    RecommendHistory selectById(@Param("recommendId") Long recommendId);

    RecommendHistory selectByReproduceKey(@Param("reproduceKey") String reproduceKey);

    List<RecommendHistory> selectHistoryByUserId(@Param("userId") Long userId);

    List<RecommendHistory> selectAll();
}
