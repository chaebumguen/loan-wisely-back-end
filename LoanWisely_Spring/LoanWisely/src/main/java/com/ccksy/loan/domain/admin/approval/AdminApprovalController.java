package com.ccksy.loan.domain.admin.approval;

import com.ccksy.loan.common.response.ApiResponse;
import com.ccksy.loan.common.security.AdminTokenClaims;
import com.ccksy.loan.domain.admin.approval.dto.ApprovalActionRequest;
import com.ccksy.loan.domain.admin.approval.dto.ApprovalDetailResponse;
import com.ccksy.loan.domain.admin.approval.dto.ApprovalListItem;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/approvals")
public class AdminApprovalController {

    private final AdminApprovalService approvalService;

    public AdminApprovalController(AdminApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @GetMapping
    public ApiResponse<List<ApprovalListItem>> list() {
        return ApiResponse.ok(approvalService.listPending());
    }

    @GetMapping("/{targetId}")
    public ApiResponse<ApprovalDetailResponse> detail(@PathVariable("targetId") String targetId) {
        return ApiResponse.ok(approvalService.getDetail(targetId));
    }

    @PostMapping("/{targetId}/approve")
    public ApiResponse<Void> approve(@PathVariable("targetId") String targetId,
                                     @RequestBody(required = false) ApprovalActionRequest request,
                                     Authentication authentication) {
        approvalService.approve(targetId, resolveActor(authentication), request == null ? "" : request.getReason());
        return ApiResponse.ok();
    }

    @PostMapping("/{targetId}/reject")
    public ApiResponse<Void> reject(@PathVariable("targetId") String targetId,
                                    @RequestBody(required = false) ApprovalActionRequest request,
                                    Authentication authentication) {
        approvalService.reject(targetId, resolveActor(authentication), request == null ? "" : request.getReason());
        return ApiResponse.ok();
    }

    private String resolveActor(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof AdminTokenClaims claims) {
            return claims.adminId();
        }
        return "system";
    }
}
