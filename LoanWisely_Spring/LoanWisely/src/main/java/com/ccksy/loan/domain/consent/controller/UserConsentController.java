// FILE: domain/consent/controller/UserConsentController.java
package com.ccksy.loan.domain.consent.controller;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ccksy.loan.common.security.UserIdResolver;
import com.ccksy.loan.domain.consent.service.UserConsentService;

/**
 * UserConsentController (v1)
 *
 * 책임:
 * - HTTP 요청/응답 처리(표현 계층)
 * - 인증 컨텍스트에서 userId 추출
 * - Request/Response DTO는 필요한 범위에서만 사용(스펙 미확정으로 primitive 기반)
 *
 * 주의:
 * - Controller에 비즈니스/정책/검증 로직을 넣지 않는다.
 * - 실제 응답 포맷 표준화는 ApiResponse에서 처리(프로젝트 합의에 맞춰 구체화)
 */
@RestController
public class UserConsentController {

    private final UserConsentService userConsentService;
    private final UserIdResolver userIdResolver;

    public UserConsentController(UserConsentService userConsentService, UserIdResolver userIdResolver) {
        this.userConsentService = Objects.requireNonNull(userConsentService, "userConsentService");
        this.userIdResolver = Objects.requireNonNull(userIdResolver, "userIdResolver");
    }

    /**
     * 동의 여부 조회
     * 예: GET /api/v1/consent/LV3_FINANCIAL
     */
    @GetMapping("/api/v1/consent/{consentType}")
    public ResponseEntity<Boolean> hasConsent(@PathVariable("consentType") String consentType) {
        Long userId = userIdResolver.requireUserId();
        boolean agreed = userConsentService.hasConsent(userId, normalize(consentType));
        return ResponseEntity.ok(agreed);
    }

    /**
     * 동의 상태 갱신
     * 예: POST /api/users/me/consents
     *
     * Request DTO 스키마가 확정되지 않아 v1에서는 Map/primitive를 사용한다.
     *
     * body 예시:
     * {
     *   "consentType": "LV3_FINANCIAL",
     *   "agreed": true
     * }
     */
    @PostMapping("/api/users/me/consents")
    public ResponseEntity<Void> saveConsent(@RequestBody ConsentBody body) {
        Long userId = userIdResolver.requireUserId();
        if (body == null) {
            throw new IllegalArgumentException("request body must not be null.");
        }

        String consentType = normalize(body.getConsentType());
        if (consentType == null) {
            throw new IllegalArgumentException("consentType must not be blank.");
        }

        userConsentService.saveConsent(userId, consentType, body.isAgreed());
        return ResponseEntity.ok().build();
    }

    private String normalize(String v) {
        if (v == null) return null;
        String s = v.trim();
        return s.isEmpty() ? null : s;
    }

    /**
     * v1: Request DTO 스키마 미확정에 따른 최소 바디 모델(단일 파일 유지)
     * - 별도 패키지 DTO로 분리하지 않고 Controller 내부에서만 사용
     */
    public static final class ConsentBody {
        private String consentType;
        private boolean agreed;

        public ConsentBody() {}

        public String getConsentType() {
            return consentType;
        }

        public void setConsentType(String consentType) {
            this.consentType = consentType;
        }

        public boolean isAgreed() {
            return agreed;
        }

        public void setAgreed(boolean agreed) {
            this.agreed = agreed;
        }
    }
}
