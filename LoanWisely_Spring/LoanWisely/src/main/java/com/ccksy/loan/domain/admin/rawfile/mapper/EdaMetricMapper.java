package com.ccksy.loan.domain.admin.rawfile.mapper;

import com.ccksy.loan.domain.admin.rawfile.entity.EdaMetric;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EdaMetricMapper {
    Long selectNextId();

    int insert(EdaMetric metric);
}
