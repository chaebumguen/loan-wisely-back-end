package com.ccksy.loan.domain.admin.rawfile.mapper;

import com.ccksy.loan.domain.admin.rawfile.entity.QualityIssue;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QualityIssueMapper {
    Long selectNextId();

    int insert(QualityIssue issue);
}
