package com.ccksy.loan.domain.admin.policy;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import com.ccksy.loan.domain.admin.policy.dto.PolicyCreateRequest;
import com.ccksy.loan.domain.admin.policy.dto.PolicyDetailResponse;
import com.ccksy.loan.domain.admin.policy.dto.PolicyDeployLogItem;
import com.ccksy.loan.domain.admin.policy.dto.PolicyListItem;
import com.ccksy.loan.domain.admin.policy.entity.PolicyDeployLog;
import com.ccksy.loan.domain.admin.policy.mapper.PolicyDeployLogMapper;
import com.ccksy.loan.domain.recommend.entity.RecoPolicy;
import com.ccksy.loan.domain.recommend.mapper.RecoPolicyMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminPolicyService {

    private static final DateTimeFormatter LIST_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final RecoPolicyMapper policyMapper;
    private final ObjectMapper objectMapper;
    private final PolicyDeployLogMapper policyDeployLogMapper;

    public AdminPolicyService(RecoPolicyMapper policyMapper,
                              ObjectMapper objectMapper,
                              PolicyDeployLogMapper policyDeployLogMapper) {
        this.policyMapper = policyMapper;
        this.objectMapper = objectMapper;
        this.policyDeployLogMapper = policyDeployLogMapper;
    }

    @Transactional(readOnly = true)
    public List<PolicyListItem> listPolicies() {
        return policyMapper.selectAll().stream()
                .map(this::toListItem)
                .toList();
    }

    @Transactional(readOnly = true)
    public PolicyDetailResponse getPolicy(Long policyId) {
        RecoPolicy policy = policyMapper.selectById(policyId);
        if (policy == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Policy not found");
        }
        return toDetail(policy);
    }

    @Transactional
    public PolicyDetailResponse createPolicy(PolicyCreateRequest request, String actorId) {
        Long nextId = policyMapper.selectNextId();
        String version = "v" + nextId;
        String valueJson = toPolicyValueJson(request, actorId);

        RecoPolicy policy = RecoPolicy.builder()
                .policyId(nextId)
                .version(version)
                .policyKey(request.getName())
                .policyValue(valueJson)
                .status("DRAFT")
                .isActive("N")
                .createdAt(LocalDateTime.now())
                .build();
        policyMapper.insert(policy);
        return toDetail(policyMapper.selectById(nextId));
    }

    @Transactional
    public PolicyDetailResponse updatePolicy(Long policyId, PolicyCreateRequest request, String actorId) {
        RecoPolicy policy = policyMapper.selectById(policyId);
        if (policy == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Policy not found");
        }
        Map<String, Object> existing = parsePolicyValue(policy.getPolicyValue());
        existing.put("description", request.getDescription());
        existing.put("rules", request.getRules());
        existing.put("validation_rules", request.getValidationRules());
        if (!existing.containsKey("author")) {
            existing.put("author", actorId);
        }
        String updatedJson = writeJson(existing);
        policyMapper.updatePolicyValue(policyId, updatedJson);
        return toDetail(policyMapper.selectById(policyId));
    }

    @Transactional
    public PolicyDetailResponse approvePolicy(Long policyId, String approverId) {
        RecoPolicy policy = policyMapper.selectById(policyId);
        if (policy == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Policy not found");
        }
        policyMapper.approvePolicy(policyId, "APPROVED", approverId, LocalDateTime.now());
        return toDetail(policyMapper.selectById(policyId));
    }

    @Transactional
    public PolicyDetailResponse deployPolicy(Long policyId, String actorId, String reason) {
        RecoPolicy policy = policyMapper.selectById(policyId);
        if (policy == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Policy not found");
        }
        if (!"APPROVED".equals(policy.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Only APPROVED policy can be deployed");
        }
        RecoPolicy previous = policyMapper.selectActive();
        policyMapper.deactivateAll();
        policyMapper.activatePolicy(policyId);
        logDeploy(policyId, previous == null ? null : previous.getPolicyId(), "DEPLOY", reason, actorId);
        return toDetail(policyMapper.selectById(policyId));
    }

    @Transactional(readOnly = true)
    public List<PolicyDeployLogItem> listDeployHistory(Long policyId) {
        return policyDeployLogMapper.selectByPolicyId(policyId).stream()
                .map(item -> new PolicyDeployLogItem(
                        item.getPolicyId(),
                        item.getPreviousPolicyId(),
                        item.getAction(),
                        item.getReason(),
                        item.getActorId(),
                        formatTime(item.getDeployedAt())
                ))
                .toList();
    }


    private PolicyListItem toListItem(RecoPolicy policy) {
        Map<String, Object> value = parsePolicyValue(policy.getPolicyValue());
        String name = value.getOrDefault("name", policy.getPolicyKey()).toString();
        String author = value.getOrDefault("author", "system").toString();
        String status = policy.getIsActive() != null && "Y".equals(policy.getIsActive())
                ? "DEPLOYED"
                : policy.getStatus();
        String updatedAt = formatTime(policy.getApprovedAt() != null ? policy.getApprovedAt() : policy.getCreatedAt());
        return new PolicyListItem(policy.getPolicyId(), name, policy.getVersion(), status, author, updatedAt);
    }

    private PolicyDetailResponse toDetail(RecoPolicy policy) {
        Map<String, Object> value = parsePolicyValue(policy.getPolicyValue());
        String name = value.getOrDefault("name", policy.getPolicyKey()).toString();
        String description = value.getOrDefault("description", "").toString();
        List<String> rules = castList(value.get("rules"));
        List<String> validationRules = castList(value.get("validation_rules"));
        String status = policy.getIsActive() != null && "Y".equals(policy.getIsActive())
                ? "DEPLOYED"
                : policy.getStatus();
        return new PolicyDetailResponse(
                policy.getPolicyId(),
                name,
                policy.getVersion(),
                status,
                description,
                rules,
                validationRules,
                policy.getApprovedBy(),
                formatTime(policy.getApprovedAt()),
                formatTime(policy.getCreatedAt())
        );
    }

    private String toPolicyValueJson(PolicyCreateRequest request, String actorId) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", request.getName());
        map.put("description", request.getDescription());
        map.put("rules", request.getRules());
        map.put("validation_rules", request.getValidationRules());
        map.put("author", actorId);
        return writeJson(map);
    }

    private Map<String, Object> parsePolicyValue(String json) {
        if (json == null || json.isBlank()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception ex) {
            return new HashMap<>();
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> castList(Object value) {
        if (value instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return List.of();
    }

    private String writeJson(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception ex) {
            return "{}";
        }
    }

    private String formatTime(LocalDateTime time) {
        return time == null ? null : time.format(LIST_TIME_FORMAT);
    }

    private void logDeploy(Long policyId, Long previousPolicyId, String action, String reason, String actorId) {
        Long nextId = policyDeployLogMapper.selectNextId();
        PolicyDeployLog log = PolicyDeployLog.builder()
                .deployId(nextId)
                .policyId(policyId)
                .previousPolicyId(previousPolicyId)
                .action(action)
                .reason(reason)
                .actorId(actorId)
                .deployedAt(LocalDateTime.now())
                .build();
        policyDeployLogMapper.insert(log);
    }
}
