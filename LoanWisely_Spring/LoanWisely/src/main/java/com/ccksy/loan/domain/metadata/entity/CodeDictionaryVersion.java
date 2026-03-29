package com.ccksy.loan.domain.metadata.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 코드 사전 버전 엔티티
 * - 설계서 기준 CODE_DICTIONARY_VERSION 테이블 대응
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CodeDictionaryVersion {

    private Long versionId;
    private Long uploadId;
    private String versionLabel;
    private String status;
    private java.time.LocalDateTime approvedAt;
    private String approvedBy;
    private String isActive;
    private LocalDateTime createdAt;
}
