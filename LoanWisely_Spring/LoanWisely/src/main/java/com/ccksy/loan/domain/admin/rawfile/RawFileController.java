package com.ccksy.loan.domain.admin.rawfile;

import com.ccksy.loan.common.response.ApiResponse;
import com.ccksy.loan.common.security.AdminTokenClaims;
import com.ccksy.loan.domain.admin.audit.AuditLogService;
import com.ccksy.loan.domain.admin.rawfile.dto.RawFileIngestResponse;
import com.ccksy.loan.domain.admin.rawfile.dto.RawFileListItem;
import com.ccksy.loan.domain.admin.rawfile.dto.RawFileNormalizeResponse;
import com.ccksy.loan.domain.admin.rawfile.dto.RawFileUploadResponse;
import com.ccksy.loan.domain.admin.rawfile.dto.RawFileValidateResponse;
import com.ccksy.loan.domain.admin.rawfile.dto.RawFileEdaResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/raw-files")
public class RawFileController {

    private final RawFileService rawFileService;
    private final AuditLogService auditLogService;

    public RawFileController(RawFileService rawFileService, AuditLogService auditLogService) {
        this.rawFileService = rawFileService;
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ApiResponse<List<RawFileListItem>> list() {
        return ApiResponse.ok(rawFileService.listRawFiles());
    }

    @PostMapping
    public ApiResponse<RawFileUploadResponse> upload(@RequestParam("file") MultipartFile file,
                                                     org.springframework.security.core.Authentication authentication) {
        RawFileUploadResponse response = rawFileService.upload(file);
        auditLogService.log(resolveActor(authentication), "RAW_FILE_UPLOAD", String.valueOf(response.getUploadId()));
        return ApiResponse.ok(response);
    }

    @PostMapping("/{id}/validate")
    public ApiResponse<RawFileValidateResponse> validate(@PathVariable("id") Long id,
                                                         org.springframework.security.core.Authentication authentication) {
        RawFileValidateResponse response = rawFileService.validate(id);
        auditLogService.log(resolveActor(authentication), "RAW_FILE_VALIDATE", String.valueOf(id));
        return ApiResponse.ok(response);
    }

    @PostMapping("/{id}/ingest")
    public ApiResponse<RawFileIngestResponse> ingest(@PathVariable("id") Long id,
                                                     org.springframework.security.core.Authentication authentication) {
        RawFileIngestResponse response = rawFileService.ingest(id);
        auditLogService.log(resolveActor(authentication), "RAW_FILE_INGEST", String.valueOf(id));
        return ApiResponse.ok(response);
    }

    @PostMapping("/{id}/normalize")
    public ApiResponse<RawFileNormalizeResponse> normalize(@PathVariable("id") Long id,
                                                           org.springframework.security.core.Authentication authentication) {
        RawFileNormalizeResponse response = rawFileService.normalize(id);
        auditLogService.log(resolveActor(authentication), "RAW_FILE_NORMALIZE", String.valueOf(id));
        return ApiResponse.ok(response);
    }

    @PostMapping("/{id}/eda")
    public ApiResponse<RawFileEdaResponse> eda(@PathVariable("id") Long id,
                                               org.springframework.security.core.Authentication authentication) {
        RawFileEdaResponse response = rawFileService.runEda(id);
        auditLogService.log(resolveActor(authentication), "RAW_FILE_EDA", String.valueOf(id));
        return ApiResponse.ok(response);
    }

    private String resolveActor(org.springframework.security.core.Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof AdminTokenClaims claims) {
            return claims.adminId();
        }
        return "system";
    }
}
