package com.ccksy.loan.domain.metadata.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 코드 사전 변경 이력 엔티티
 * - 설계서 기준 CODE_DICTIONARY_DIFF 테이블 대응
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CodeDictionaryDiff {

    private Long diffId;
    private Long preVersionId;
    private Long postVersionId;

    private String changeType;
    private String columnCode;

    private String beforeJsonPath;
    private String afterJsonPath;

    private LocalDateTime createdAt;
}
