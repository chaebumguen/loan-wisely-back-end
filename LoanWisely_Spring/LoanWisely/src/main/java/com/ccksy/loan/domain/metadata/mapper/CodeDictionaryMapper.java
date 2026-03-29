package com.ccksy.loan.domain.metadata.mapper;

import com.ccksy.loan.domain.metadata.entity.CodeDictionary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CodeDictionaryMapper {

    int insert(CodeDictionary dict);

    CodeDictionary selectById(@Param("dictId") Long dictId);

    List<CodeDictionary> selectByVersionId(@Param("versionId") Long versionId);

    int deleteByVersionId(@Param("versionId") Long versionId);
}
