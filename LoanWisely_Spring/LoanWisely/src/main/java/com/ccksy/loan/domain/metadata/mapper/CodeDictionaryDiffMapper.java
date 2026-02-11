package com.ccksy.loan.domain.metadata.mapper;

import com.ccksy.loan.domain.metadata.entity.CodeDictionaryDiff;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CodeDictionaryDiffMapper {

    int insert(CodeDictionaryDiff diff);

    List<CodeDictionaryDiff> selectByVersionIds(@Param("preVersionId") Long preVersionId,
                                                @Param("postVersionId") Long postVersionId);
}
