package com.ccksy.loan.domain.metadata.service;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import com.ccksy.loan.domain.metadata.dto.admin.CreditDictionaryItemRequest;
import com.ccksy.loan.domain.metadata.dto.admin.VersionListItem;
import com.ccksy.loan.domain.metadata.entity.CodeDictionary;
import com.ccksy.loan.domain.metadata.entity.CodeDictionaryVersion;
import com.ccksy.loan.domain.metadata.entity.CodeDictionaryDiff;
import com.ccksy.loan.domain.metadata.mapper.CodeDictionaryDiffMapper;
import com.ccksy.loan.domain.metadata.mapper.CodeDictionaryMapper;
import com.ccksy.loan.domain.metadata.mapper.CodeDictionaryVersionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminCodeDictionaryService {

    private static final DateTimeFormatter LIST_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final CodeDictionaryVersionMapper versionMapper;
    private final CodeDictionaryMapper dictMapper;
    private final CodeDictionaryDiffMapper diffMapper;
    private final ObjectMapper objectMapper;
    private final Path diffDir;

    public AdminCodeDictionaryService(CodeDictionaryVersionMapper versionMapper,
                                      CodeDictionaryMapper dictMapper,
                                      CodeDictionaryDiffMapper diffMapper,
                                      ObjectMapper objectMapper,
                                      @Value("${storage.explain-dir}") String explainDir) {
        this.versionMapper = versionMapper;
        this.dictMapper = dictMapper;
        this.diffMapper = diffMapper;
        this.objectMapper = objectMapper;
        this.diffDir = Paths.get(explainDir).resolve("code-diff");
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

        List<CodeDictionary> beforeItems = dictMapper.selectByVersionId(versionId);
        Map<String, CodeDictionary> beforeMap = new HashMap<>();
        for (CodeDictionary item : beforeItems) {
            if (item.getColumnCode() != null) {
                beforeMap.put(item.getColumnCode(), item);
            }
        }

        Map<String, CreditDictionaryItemRequest> afterMap = new HashMap<>();
        if (items != null) {
            for (CreditDictionaryItemRequest item : items) {
                if (item.getColumnCode() != null) {
                    afterMap.put(item.getColumnCode(), item);
                }
            }
        }

        dictMapper.deleteByVersionId(versionId);
        if (items == null) {
            writeDiffs(versionId, beforeMap, afterMap);
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

        writeDiffs(versionId, beforeMap, afterMap);
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

    private void writeDiffs(Long versionId,
                            Map<String, CodeDictionary> beforeMap,
                            Map<String, CreditDictionaryItemRequest> afterMap) {
        try {
            Files.createDirectories(diffDir);
            for (String columnCode : unionKeys(beforeMap, afterMap)) {
                CodeDictionary before = beforeMap.get(columnCode);
                CreditDictionaryItemRequest after = afterMap.get(columnCode);
                String changeType = resolveChangeType(before, after);
                if (changeType == null) {
                    continue;
                }

                String beforePath = before == null ? null : writeJson("before", versionId, columnCode, before);
                String afterPath = after == null ? null : writeJson("after", versionId, columnCode, after);

                CodeDictionaryDiff diff = CodeDictionaryDiff.builder()
                        .preVersionId(versionId)
                        .postVersionId(versionId)
                        .changeType(changeType)
                        .columnCode(columnCode)
                        .beforeJsonPath(beforePath)
                        .afterJsonPath(afterPath)
                        .createdAt(LocalDateTime.now())
                        .build();
                diffMapper.insert(diff);
            }
        } catch (Exception ex) {
            // If diff logging fails, keep main update successful.
        }
    }

    private List<String> unionKeys(Map<String, CodeDictionary> beforeMap,
                                   Map<String, CreditDictionaryItemRequest> afterMap) {
        Map<String, Boolean> union = new HashMap<>();
        for (String key : beforeMap.keySet()) {
            union.put(key, Boolean.TRUE);
        }
        for (String key : afterMap.keySet()) {
            union.put(key, Boolean.TRUE);
        }
        return union.keySet().stream().toList();
    }

    private String resolveChangeType(CodeDictionary before, CreditDictionaryItemRequest after) {
        if (before == null && after == null) {
            return null;
        }
        if (before == null) {
            return "ADD";
        }
        if (after == null) {
            return "DEL";
        }
        if (equalsItem(before, after)) {
            return null;
        }
        return "MOD";
    }

    private boolean equalsItem(CodeDictionary before, CreditDictionaryItemRequest after) {
        return safeEq(before.getColumnName(), after.getColumnName())
                && safeEq(before.getColumnDesc(), after.getColumnDesc())
                && safeEq(before.getDataType(), after.getDataType())
                && safeEq(before.getIsRequired(), Boolean.TRUE.equals(after.getIsRequired()) ? "Y" : "N");
    }

    private boolean safeEq(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    private String writeJson(String prefix, Long versionId, String columnCode, Object payload) {
        try {
            String name = prefix + "-" + versionId + "-" + columnCode + "-" + System.currentTimeMillis() + ".json";
            Path path = diffDir.resolve(name);
            objectMapper.writeValue(path.toFile(), payload);
            return path.toString();
        } catch (Exception ex) {
            return null;
        }
    }
}
