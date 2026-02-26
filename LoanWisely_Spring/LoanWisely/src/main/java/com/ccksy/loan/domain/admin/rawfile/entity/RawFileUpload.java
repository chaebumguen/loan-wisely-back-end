package com.ccksy.loan.domain.admin.rawfile.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RawFileUpload {
    private Long uploadId;
    private String fileName;
    private String fileHash;
    private Long uploaderId;
    private String storedPath;
    private String status;
    private LocalDateTime uploadedAt;
}
