package com.ccksy.loan.domain.admin.approval;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import com.ccksy.loan.domain.admin.approval.dto.ApprovalDetailResponse;
import com.ccksy.loan.domain.admin.approval.dto.ApprovalListItem;
import com.ccksy.loan.domain.admin.audit.AuditLogService;
import com.ccksy.loan.domain.metadata.entity.CodeDictionaryVersion;
import com.ccksy.loan.domain.metadata.entity.FinancialMetaVersion;
import com.ccksy.loan.domain.metadata.mapper.CodeDictionaryVersionMapper;
import com.ccksy.loan.domain.metadata.mapper.FinancialMetaVersionMapper;
import com.ccksy.loan.domain.recommend.entity.RecoPolicy;
import com.ccksy.loan.domain.recommend.mapper.RecoPolicyMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AdminApprovalService {

    private static final DateTimeFormatter LIST_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String TYPE_POLICY = "POLICY";
    private static final String TYPE_CREDIT_META = "CREDIT_META";
    private static final String TYPE_FIN_META = "FIN_META";

    private final RecoPolicyMapper policyMapper;
    private final CodeDictionaryVersionMapper codeDictMapper;
    private final FinancialMetaVersionMapper finMetaMapper;
    private final ObjectMapper objectMapper;
    private final AuditLogService auditLogService;
    private final com.ccksy.loan.domain.admin.approval.mapper.ApprovalLogMapper approvalLogMapper;

    public AdminApprovalService(RecoPolicyMapper policyMapper,
                                CodeDictionaryVersionMapper codeDictMapper,
                                FinancialMetaVersionMapper finMetaMapper,
                                ObjectMapper objectMapper,
                                AuditLogService auditLogService,
                                com.ccksy.loan.domain.admin.approval.mapper.ApprovalLogMapper approvalLogMapper) {
        this.policyMapper = policyMapper;
        this.codeDictMapper = codeDictMapper;
        this.finMetaMapper = finMetaMapper;
        this.objectMapper = objectMapper;
        this.auditLogService = auditLogService;
        this.approvalLogMapper = approvalLogMapper;
    }

    @Transactional(readOnly = true)
    public List<ApprovalListItem> listPending() {
        List<ApprovalListItem> items = new ArrayList<>();

        for (RecoPolicy policy : policyMapper.selectAll()) {
            if ("DRAFT".equals(policy.getStatus())) {
                items.add(new ApprovalListItem(
                        formatTargetId(TYPE_POLICY, policy.getPolicyId()),
                        TYPE_POLICY,
                        "PENDING",
                        formatTime(policy.getCreatedAt())
                ));
            }
        }

        for (CodeDictionaryVersion version : codeDictMapper.selectAll()) {
            if ("DRAFT".equals(version.getStatus())) {
                items.add(new ApprovalListItem(
                        formatTargetId(TYPE_CREDIT_META, version.getVersionId()),
                        TYPE_CREDIT_META,
                        "PENDING",
                        formatTime(version.getCreatedAt())
                ));
            }
        }

        for (FinancialMetaVersion version : finMetaMapper.selectAll()) {
            if ("DRAFT".equals(version.getStatus())) {
                items.add(new ApprovalListItem(
                        formatTargetId(TYPE_FIN_META, version.getVersionId()),
                        TYPE_FIN_META,
                        "PENDING",
                        formatTime(version.getCreatedAt())
                ));
            }
        }

        return items;
    }

    @Transactional(readOnly = true)
    public ApprovalDetailResponse getDetail(String targetId) {
        Target target = parseTarget(targetId);
        if (TYPE_POLICY.equals(target.type)) {
            RecoPolicy policy = policyMapper.selectById(target.id);
            if (policy == null) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, "Policy not found");
            }
            return new ApprovalDetailResponse(
                    targetId,
                    TYPE_POLICY,
                    mapStatus(policy.getStatus()),
                    extractPolicyAuthor(policy),
                    formatTime(policy.getCreatedAt())
            );
        }
        if (TYPE_CREDIT_META.equals(target.type)) {
            CodeDictionaryVersion version = codeDictMapper.selectById(target.id);
            if (version == null) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, "Credit meta version not found");
            }
            return new ApprovalDetailResponse(
                    targetId,
                    TYPE_CREDIT_META,
                    mapStatus(version.getStatus()),
                    "system",
                    formatTime(version.getCreatedAt())
            );
        }
        if (TYPE_FIN_META.equals(target.type)) {
            FinancialMetaVersion version = finMetaMapper.selectById(target.id);
            if (version == null) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, "Financial meta version not found");
            }
            return new ApprovalDetailResponse(
                    targetId,
                    TYPE_FIN_META,
                    mapStatus(version.getStatus()),
                    "system",
                    formatTime(version.getCreatedAt())
            );
        }
        throw new BusinessException(ErrorCode.INVALID_REQUEST, "Invalid approval type");
    }

    @Transactional
    public void approve(String targetId, String actor, String reason) {
        Target target = parseTarget(targetId);
        logApproval(targetId, "APPROVE", reason, actor);
        if (TYPE_POLICY.equals(target.type)) {
            policyMapper.approvePolicy(target.id, "APPROVED", actor, LocalDateTime.now());
            auditLogService.log(actor, "APPROVAL_POLICY", reasonOrDefault(reason, "APPROVED"));
            return;
        }
        if (TYPE_CREDIT_META.equals(target.type)) {
            codeDictMapper.deactivateAll();
            codeDictMapper.updateStatus(target.id, "APPROVED", actor, LocalDateTime.now(), "Y");
            auditLogService.log(actor, "APPROVAL_CREDIT_META", reasonOrDefault(reason, "APPROVED"));
            return;
        }
        if (TYPE_FIN_META.equals(target.type)) {
            finMetaMapper.deactivateAll();
            finMetaMapper.updateStatus(target.id, "APPROVED", actor, LocalDateTime.now(), "Y");
            auditLogService.log(actor, "APPROVAL_FIN_META", reasonOrDefault(reason, "APPROVED"));
            return;
        }
        throw new BusinessException(ErrorCode.INVALID_REQUEST, "Invalid approval type");
    }

    @Transactional
    public void reject(String targetId, String actor, String reason) {
        Target target = parseTarget(targetId);
        logApproval(targetId, "REJECT", reason, actor);
        if (TYPE_POLICY.equals(target.type)) {
            policyMapper.approvePolicy(target.id, "REJECTED", actor, LocalDateTime.now());
            auditLogService.log(actor, "REJECT_POLICY", reasonOrDefault(reason, "REJECTED"));
            return;
        }
        if (TYPE_CREDIT_META.equals(target.type)) {
            codeDictMapper.updateStatus(target.id, "REJECTED", actor, LocalDateTime.now(), "N");
            auditLogService.log(actor, "REJECT_CREDIT_META", reasonOrDefault(reason, "REJECTED"));
            return;
        }
        if (TYPE_FIN_META.equals(target.type)) {
            finMetaMapper.updateStatus(target.id, "REJECTED", actor, LocalDateTime.now(), "N");
            auditLogService.log(actor, "REJECT_FIN_META", reasonOrDefault(reason, "REJECTED"));
            return;
        }
        throw new BusinessException(ErrorCode.INVALID_REQUEST, "Invalid approval type");
    }

    private String reasonOrDefault(String reason, String fallback) {
        if (reason == null || reason.isBlank()) {
            return fallback;
        }
        return reason;
    }

    private String extractPolicyAuthor(RecoPolicy policy) {
        Map<String, Object> value = parsePolicyValue(policy.getPolicyValue());
        Object author = value.get("author");
        return author == null ? "system" : author.toString();
    }

    private Map<String, Object> parsePolicyValue(String json) {
        if (json == null || json.isBlank()) {
            return java.util.Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception ex) {
            return java.util.Collections.emptyMap();
        }
    }

    private String mapStatus(String status) {
        if ("DRAFT".equals(status)) {
            return "PENDING";
        }
        if ("APPROVED".equals(status)) {
            return "APPROVED";
        }
        if ("REJECTED".equals(status) || "DEPRECATED".equals(status)) {
            return "REJECTED";
        }
        return status == null ? "UNKNOWN" : status;
    }

    private String formatTargetId(String type, Long id) {
        return type + "-" + id;
    }

    private Target parseTarget(String targetId) {
        if (targetId == null || !targetId.contains("-")) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Invalid target id");
        }
        int idx = targetId.indexOf("-");
        String type = targetId.substring(0, idx);
        String idPart = targetId.substring(idx + 1);
        try {
            Long id = Long.parseLong(idPart);
            return new Target(type, id);
        } catch (NumberFormatException ex) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Invalid target id");
        }
    }

    private String formatTime(LocalDateTime time) {
        return time == null ? null : time.format(LIST_TIME_FORMAT);
    }

    private void logApproval(String targetId, String action, String reason, String actor) {
        Long nextId = approvalLogMapper.selectNextId();
        com.ccksy.loan.domain.admin.approval.entity.ApprovalLog log =
                com.ccksy.loan.domain.admin.approval.entity.ApprovalLog.builder()
                        .approvalId(nextId)
                        .targetId(targetId)
                        .action(action)
                        .reason(reason)
                        .actorId(actor)
                        .createdAt(LocalDateTime.now())
                        .build();
        approvalLogMapper.insert(log);
    }

    private record Target(String type, Long id) {
    }
}
