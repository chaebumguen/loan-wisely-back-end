package com.ccksy.loan.domain.admin.search;

import com.ccksy.loan.common.response.ApiResponse;
import com.ccksy.loan.infra.elasticsearch.EsRecoPolicyService;
import com.ccksy.loan.infra.elasticsearch.EsReindexService;
import com.ccksy.loan.infra.elasticsearch.EsRecommendHistoryService;
import com.ccksy.loan.infra.elasticsearch.dto.EsRecoPolicySearchItem;
import com.ccksy.loan.infra.elasticsearch.dto.EsReindexResponse;
import com.ccksy.loan.infra.elasticsearch.dto.EsRecommendHistorySearchItem;
import com.ccksy.loan.infra.elasticsearch.dto.EsSearchResponse;
import com.ccksy.loan.domain.user.auth.entity.UserAuth;
import com.ccksy.loan.domain.user.auth.mapper.UserAuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/es")
public class AdminEsSearchController {

    private final EsRecommendHistoryService esRecommendHistoryService;
    private final EsRecoPolicyService esRecoPolicyService;
    private final EsReindexService esReindexService;
    private final UserAuthMapper userAuthMapper;

    @GetMapping("/recommend-histories")
    public ApiResponse<EsSearchResponse<EsRecommendHistorySearchItem>> searchRecommendHistories(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "loginId", required = false) String loginId,
            @RequestParam(value = "policyVersion", required = false) String policyVersion,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size
    ) {
        Long resolvedUserId = userId;
        if (loginId != null && !loginId.isBlank()) {
            UserAuth auth = userAuthMapper.selectByUsername(loginId);
            if (auth == null || auth.getUserId() == null) {
                return ApiResponse.ok(new EsSearchResponse<>(0, Collections.emptyList()));
            }
            if (resolvedUserId != null && !resolvedUserId.equals(auth.getUserId())) {
                return ApiResponse.ok(new EsSearchResponse<>(0, Collections.emptyList()));
            }
            resolvedUserId = auth.getUserId();
        }

        return ApiResponse.ok(esRecommendHistoryService.search(resolvedUserId, policyVersion, keyword, from, to, page, size));
    }

    @GetMapping("/reco-policies")
    public ApiResponse<EsSearchResponse<EsRecoPolicySearchItem>> searchRecoPolicies(
            @RequestParam(value = "version", required = false) String version,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "isActive", required = false) String isActive,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size
    ) {
        return ApiResponse.ok(esRecoPolicyService.search(version, status, isActive, page, size));
    }

    @PostMapping("/reindex")
    public ApiResponse<EsReindexResponse> reindexAll() {
        return ApiResponse.ok(esReindexService.rebuildAll());
    }
}
