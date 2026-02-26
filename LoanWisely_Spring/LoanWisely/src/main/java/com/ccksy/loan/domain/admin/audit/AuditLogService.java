package com.ccksy.loan.domain.admin.audit;

import com.ccksy.loan.domain.admin.audit.dto.AuditLogItem;
import com.ccksy.loan.domain.admin.audit.entity.AuditLog;
import com.ccksy.loan.domain.admin.audit.mapper.AuditLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AuditLogService {

    private static final DateTimeFormatter LIST_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final AuditLogMapper auditLogMapper;

    public AuditLogService(AuditLogMapper auditLogMapper) {
        this.auditLogMapper = auditLogMapper;
    }

    @Transactional
    public void log(String actorId, String action, String target) {
        Long nextId = auditLogMapper.selectNextId();
        AuditLog log = AuditLog.builder()
                .auditId(nextId)
                .actorId(actorId)
                .action(action)
                .target(target)
                .createdAt(LocalDateTime.now())
                .build();
        auditLogMapper.insert(log);
    }

    @Transactional
    public void logDetail(String actorId, String actorRoles, String action, String target, String detailJson) {
        Long nextId = auditLogMapper.selectNextId();
        AuditLog log = AuditLog.builder()
                .auditId(nextId)
                .actorId(actorId)
                .actorRoles(actorRoles)
                .action(action)
                .target(target)
                .detailJson(detailJson)
                .createdAt(LocalDateTime.now())
                .build();
        auditLogMapper.insert(log);
    }

    @Transactional(readOnly = true)
    public List<AuditLogItem> list() {
        return auditLogMapper.selectAll().stream()
                .map(log -> new AuditLogItem(
                        log.getActorId(),
                        log.getAction(),
                        log.getTarget(),
                        log.getCreatedAt() == null ? null : log.getCreatedAt().format(LIST_TIME_FORMAT)
                ))
                .toList();
    }
}
