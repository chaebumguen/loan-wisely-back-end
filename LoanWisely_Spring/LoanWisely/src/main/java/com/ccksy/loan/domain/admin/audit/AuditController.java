package com.ccksy.loan.domain.admin.audit;

import com.ccksy.loan.common.response.ApiResponse;
import com.ccksy.loan.domain.admin.audit.dto.AuditLogItem;
import com.ccksy.loan.common.security.AdminTokenClaims;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit")
public class AuditController {

    private final AuditLogService auditLogService;

    public AuditController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping("/summary")
    public ApiResponse<List<AuditLogItem>> summary() {
        return ApiResponse.ok(auditLogService.list());
    }

    @PostMapping("/event")
    public ApiResponse<String> logEvent(@RequestParam("action") String action,
                                        @RequestParam("target") String target,
                                        @RequestParam(value = "detail", required = false) String detail,
                                        Authentication authentication) {
        String actor = resolveActor(authentication);
        String roles = resolveRoles(authentication);
        auditLogService.logDetail(actor, roles, action, target, detail);
        return ApiResponse.ok("OK");
    }

    private String resolveActor(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof AdminTokenClaims claims) {
            return claims.adminId();
        }
        return "system";
    }

    private String resolveRoles(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof AdminTokenClaims claims) {
            return String.join(",", claims.roles());
        }
        return "";
    }
}
