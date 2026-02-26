package com.ccksy.loan.domain.metadata.service;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import com.ccksy.loan.domain.metadata.dto.admin.VersionListItem;
import com.ccksy.loan.domain.metadata.entity.FinancialMetaVersion;
import com.ccksy.loan.domain.metadata.mapper.FinancialMetaVersionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AdminFinancialMetaService {

    private static final DateTimeFormatter LIST_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final FinancialMetaVersionMapper versionMapper;

    public AdminFinancialMetaService(FinancialMetaVersionMapper versionMapper) {
        this.versionMapper = versionMapper;
    }

    @Transactional(readOnly = true)
    public List<VersionListItem> listVersions() {
        return versionMapper.selectAll().stream()
                .map(v -> new VersionListItem(
                        v.getVersionId(),
                        v.getStatus(),
                        formatTime(v.getApprovedAt() != null ? v.getApprovedAt() : v.getCreatedAt())
                ))
                .toList();
    }

    @Transactional
    public FinancialMetaVersion createVersion(String versionLabel, String metaJson) {
        Long nextId = versionMapper.selectNextId();
        FinancialMetaVersion version = FinancialMetaVersion.builder()
                .versionId(nextId)
                .versionLabel(versionLabel)
                .metaJson(metaJson)
                .status("DRAFT")
                .approvedAt(null)
                .approvedBy(null)
                .isActive("N")
                .createdAt(LocalDateTime.now())
                .build();
        versionMapper.insert(version);
        return versionMapper.selectById(nextId);
    }

    @Transactional
    public FinancialMetaVersion approve(Long versionId, String approvedBy) {
        FinancialMetaVersion version = versionMapper.selectById(versionId);
        if (version == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Version not found");
        }
        versionMapper.deactivateAll();
        versionMapper.updateStatus(versionId, "APPROVED", approvedBy, LocalDateTime.now(), "Y");
        return versionMapper.selectById(versionId);
    }

    private String formatTime(LocalDateTime time) {
        return time == null ? null : time.format(LIST_TIME_FORMAT);
    }
}
