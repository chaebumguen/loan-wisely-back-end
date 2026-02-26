package com.ccksy.loan.domain.metadata.controller;

import com.ccksy.loan.common.response.ApiResponse;
import com.ccksy.loan.common.security.AdminTokenClaims;
import com.ccksy.loan.domain.admin.audit.AuditLogService;
import com.ccksy.loan.domain.metadata.dto.admin.CreditDictionaryUpdateRequest;
import com.ccksy.loan.domain.metadata.dto.admin.VersionCreateRequest;
import com.ccksy.loan.domain.metadata.dto.admin.VersionCreateResponse;
import com.ccksy.loan.domain.metadata.dto.admin.VersionListItem;
import com.ccksy.loan.domain.metadata.entity.CodeDictionaryVersion;
import com.ccksy.loan.domain.metadata.service.AdminCodeDictionaryService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/metadata/credit-dictionary")
public class AdminCodeDictionaryController {

    private final AdminCodeDictionaryService adminService;
    private final AuditLogService auditLogService;

    public AdminCodeDictionaryController(AdminCodeDictionaryService adminService,
                                         AuditLogService auditLogService) {
        this.adminService = adminService;
        this.auditLogService = auditLogService;
    }

    @GetMapping("/versions")
    public ApiResponse<List<VersionListItem>> listVersions() {
        return ApiResponse.ok(adminService.listVersions());
    }

    @PostMapping("/versions")
    public ApiResponse<VersionCreateResponse> createVersion(@RequestBody(required = false) VersionCreateRequest request) {
        Long baseVersionId = request == null ? null : request.getBaseVersionId();
        String label = request == null ? null : request.getVersionLabel();
        CodeDictionaryVersion created = adminService.createVersion(baseVersionId, label);
        return ApiResponse.ok(new VersionCreateResponse(created.getVersionId(), created.getStatus()));
    }

    @PutMapping("/versions/{versionId}")
    public ApiResponse<Void> updateVersion(@PathVariable("versionId") Long versionId,
                                           @RequestBody CreditDictionaryUpdateRequest request,
                                           Authentication authentication) {
        adminService.updateItems(versionId, request.getItems());
        auditLogService.log(resolveActor(authentication), "CODE_DICT_UPDATE", String.valueOf(versionId));
        return ApiResponse.ok();
    }

    @PostMapping("/versions/{versionId}/approve")
    public ApiResponse<VersionCreateResponse> approveVersion(@PathVariable("versionId") Long versionId,
                                                             Authentication authentication) {
        String approver = resolveActor(authentication);
        CodeDictionaryVersion approved = adminService.approve(versionId, approver);
        auditLogService.log(approver, "CODE_DICT_APPROVE", String.valueOf(versionId));
        return ApiResponse.ok(new VersionCreateResponse(approved.getVersionId(), approved.getStatus()));
    }

    private String resolveActor(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof AdminTokenClaims claims) {
            return claims.adminId();
        }
        return "system";
    }
}
