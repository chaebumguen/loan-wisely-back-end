package com.ccksy.loan.domain.metadata.controller;

import com.ccksy.loan.common.response.ApiResponse;
import com.ccksy.loan.common.security.AdminTokenClaims;
import com.ccksy.loan.domain.admin.audit.AuditLogService;
import com.ccksy.loan.domain.metadata.dto.admin.VersionCreateRequest;
import com.ccksy.loan.domain.metadata.dto.admin.VersionCreateResponse;
import com.ccksy.loan.domain.metadata.dto.admin.VersionListItem;
import com.ccksy.loan.domain.metadata.entity.FinancialMetaVersion;
import com.ccksy.loan.domain.metadata.service.AdminFinancialMetaService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/metadata/financial-meta")
public class AdminFinancialMetaController {

    private final AdminFinancialMetaService adminService;
    private final AuditLogService auditLogService;

    public AdminFinancialMetaController(AdminFinancialMetaService adminService,
                                        AuditLogService auditLogService) {
        this.adminService = adminService;
        this.auditLogService = auditLogService;
    }

    @GetMapping("/versions")
    public ApiResponse<List<VersionListItem>> listVersions() {
        return ApiResponse.ok(adminService.listVersions());
    }

    @PostMapping("/versions")
    public ApiResponse<VersionCreateResponse> createVersion(@RequestBody(required = false) VersionCreateRequest request,
                                                            Authentication authentication) {
        String label = request == null ? null : request.getVersionLabel();
        FinancialMetaVersion created = adminService.createVersion(label, null);
        auditLogService.log(resolveActor(authentication), "FIN_META_CREATE", String.valueOf(created.getVersionId()));
        return ApiResponse.ok(new VersionCreateResponse(created.getVersionId(), created.getStatus()));
    }

    @PostMapping("/versions/{versionId}/approve")
    public ApiResponse<VersionCreateResponse> approveVersion(@PathVariable("versionId") Long versionId,
                                                             Authentication authentication) {
        String approver = resolveActor(authentication);
        FinancialMetaVersion approved = adminService.approve(versionId, approver);
        auditLogService.log(approver, "FIN_META_APPROVE", String.valueOf(versionId));
        return ApiResponse.ok(new VersionCreateResponse(approved.getVersionId(), approved.getStatus()));
    }

    private String resolveActor(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof AdminTokenClaims claims) {
            return claims.adminId();
        }
        return "system";
    }
}
