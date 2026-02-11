package com.ccksy.loan.domain.metadata.service;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import com.ccksy.loan.domain.metadata.dto.admin.CreditDictionaryItemRequest;
import com.ccksy.loan.domain.metadata.dto.admin.VersionListItem;
import com.ccksy.loan.domain.metadata.entity.CodeDictionary;
import com.ccksy.loan.domain.metadata.entity.CodeDictionaryVersion;
import com.ccksy.loan.domain.metadata.mapper.CodeDictionaryMapper;
import com.ccksy.loan.domain.metadata.mapper.CodeDictionaryVersionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AdminCodeDictionaryService {

    private static final DateTimeFormatter LIST_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final CodeDictionaryVersionMapper versionMapper;
    private final CodeDictionaryMapper dictMapper;

    public AdminCodeDictionaryService(CodeDictionaryVersionMapper versionMapper,
                                      CodeDictionaryMapper dictMapper) {
        this.versionMapper = versionMapper;
        this.dictMapper = dictMapper;
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
    public CodeDictionaryVersion createVersion(Long baseVersionId, String versionLabel) {
        Long nextId = versionMapper.selectNextId();
        CodeDictionaryVersion version = CodeDictionaryVersion.builder()
                .versionId(nextId)
                .uploadId(null)
                .versionLabel(versionLabel)
                .status("DRAFT")
                .approvedAt(null)
                .approvedBy(null)
                .isActive("N")
                .createdAt(LocalDateTime.now())
                .build();
        versionMapper.insert(version);

        if (baseVersionId != null) {
            List<CodeDictionary> baseItems = dictMapper.selectByVersionId(baseVersionId);
            for (CodeDictionary item : baseItems) {
                CodeDictionary copy = item.toBuilder()
                        .dictId(null)
                        .versionId(nextId)
                        .createdAt(LocalDateTime.now())
                        .build();
                dictMapper.insert(copy);
            }
        }

        return versionMapper.selectById(nextId);
    }

    @Transactional
    public void updateItems(Long versionId, List<CreditDictionaryItemRequest> items) {
        CodeDictionaryVersion version = versionMapper.selectById(versionId);
        if (version == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Version not found");
        }
        if (!"DRAFT".equals(version.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Only DRAFT version can be updated");
        }

        dictMapper.deleteByVersionId(versionId);
        if (items == null) {
            return;
        }
        for (CreditDictionaryItemRequest item : items) {
            CodeDictionary entity = CodeDictionary.builder()
                    .versionId(versionId)
                    .columnCode(item.getColumnCode())
                    .columnName(item.getColumnName())
                    .columnDesc(item.getColumnDesc())
                    .dataType(item.getDataType())
                    .isRequired(Boolean.TRUE.equals(item.getIsRequired()) ? "Y" : "N")
                    .createdAt(LocalDateTime.now())
                    .build();
            dictMapper.insert(entity);
        }
    }

    @Transactional
    public CodeDictionaryVersion approve(Long versionId, String approvedBy) {
        CodeDictionaryVersion version = versionMapper.selectById(versionId);
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
