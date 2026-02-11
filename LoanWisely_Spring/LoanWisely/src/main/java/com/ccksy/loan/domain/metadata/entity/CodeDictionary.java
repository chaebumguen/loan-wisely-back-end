package com.ccksy.loan.domain.metadata.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 코드 사전 엔티티
 * - 설계서 기준 CODE_DICTIONARY 테이블 대응
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CodeDictionary {

    private Long dictId;
    private Long versionId;

    private String columnCode;
    private String columnName;
    private String columnDesc;

    private String largeCategoryCodeValueId;
    private String mideumCategoryCodeValueId;
    private String smallCategoryCodeValueId;

    private String isRequired;
    private String dataType;

    private LocalDateTime createdAt;
}
