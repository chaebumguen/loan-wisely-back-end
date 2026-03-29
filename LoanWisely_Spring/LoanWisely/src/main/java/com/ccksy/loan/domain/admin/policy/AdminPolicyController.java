package com.ccksy.loan.domain.admin.policy;

import com.ccksy.loan.common.response.ApiResponse;
import com.ccksy.loan.common.security.AdminTokenClaims;
import com.ccksy.loan.domain.admin.audit.AuditLogService;
import com.ccksy.loan.domain.admin.policy.dto.PolicyCreateRequest;
import com.ccksy.loan.domain.admin.policy.dto.PolicyDetailResponse;
import com.ccksy.loan.domain.admin.policy.dto.PolicyDeployLogItem;
import com.ccksy.loan.domain.admin.policy.dto.PolicyListItem;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/policies")
public class AdminPolicyController {

    private final AdminPolicyService policyService;
    private final AuditLogService auditLogService;

    public AdminPolicyController(AdminPolicyService policyService,
                                 AuditLogService auditLogService) {
        this.policyService = policyService;
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ApiResponse<List<PolicyListItem>> list() {
        return ApiResponse.ok(policyService.listPolicies());
    }

    @GetMapping("/{id}")
    public ApiResponse<PolicyDetailResponse> get(@PathVariable("id") Long id) {
        return ApiResponse.ok(policyService.getPolicy(id));
    }

    @PostMapping
    public ApiResponse<PolicyDetailResponse> create(@RequestBody PolicyCreateRequest request,
                                                    Authentication authentication) {
        String actor = resolveActor(authentication);
        PolicyDetailResponse response = policyService.createPolicy(request, actor);
        auditLogService.log(actor, "POLICY_CREATE", String.valueOf(response.getId()));
        return ApiResponse.ok(response);
    }

    @PutMapping("/{id}")
    public ApiResponse<PolicyDetailResponse> update(@PathVariable("id") Long id,
                                                    @RequestBody PolicyCreateRequest request,
                                                    Authentication authentication) {
        String actor = resolveActor(authentication);
        PolicyDetailResponse response = policyService.updatePolicy(id, request, actor);
        auditLogService.log(actor, "POLICY_UPDATE", String.valueOf(id));
        return ApiResponse.ok(response);
    }

    @PostMapping("/{id}/approve")
    public ApiResponse<PolicyDetailResponse> approve(@PathVariable("id") Long id,
                                                     Authentication authentication) {
        String actor = resolveActor(authentication);
        PolicyDetailResponse response = policyService.approvePolicy(id, actor);
        auditLogService.log(actor, "POLICY_APPROVE", String.valueOf(id));
        return ApiResponse.ok(response);
    }

    @PostMapping("/{id}/deploy")
    public ApiResponse<PolicyDetailResponse> deploy(@PathVariable("id") Long id,
                                                    Authentication authentication) {
        String actor = resolveActor(authentication);
        PolicyDetailResponse response = policyService.deployPolicy(id, actor, "");
        auditLogService.log(actor, "POLICY_DEPLOY", String.valueOf(id));
        return ApiResponse.ok(response);
    }

    @GetMapping("/{id}/deploy-history")
    public ApiResponse<List<PolicyDeployLogItem>> deployHistory(@PathVariable("id") Long id) {
        return ApiResponse.ok(policyService.listDeployHistory(id));
    }

    private String resolveActor(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof AdminTokenClaims claims) {
            return claims.adminId();
        }
        return "system";
    }
}
