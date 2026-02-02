//package com.ccksy.loan.domain.consent.controller;
//
//import com.ccksy.loan.domain.consent.dto.request.UserConsentRequest;
//import com.ccksy.loan.domain.consent.dto.response.UserConsentResponse;
//import com.ccksy.loan.domain.consent.service.UserConsentService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
///**
// * (Facade) 입력 레벨별 동의 관리 진입점
// * API 명세(발췌): POST/GET /api/users/me/consents:contentReference[oaicite:11]{index=11}
// */
//@RestController
//@RequestMapping("/api/users/me/consents")
//public class UserConsentController {
//
//    private final UserConsentService userConsentService;
//
//    public UserConsentController(UserConsentService userConsentService) {
//        this.userConsentService = userConsentService;
//    }
//
//    /**
//     * 동의 생성
//     * - 목적태그(purposeTags)는 DB에 JSON으로 넣지 않고 파일 경로로 저장(요청사항 반영)
//     */
//    @PostMapping
//    public ResponseEntity<UserConsentResponse> createConsent(
//            @RequestHeader("X-USER-ID") Long userId,
//            @RequestBody UserConsentRequest request
//    ) {
//        UserConsentResponse created = userConsentService.createConsent(userId, request);
//        return ResponseEntity.ok(created);
//    }
//
//    /**
//     * 현재 유효 동의 조회(만료/비활성 제외)
//     */
//    @GetMapping
//    public ResponseEntity<List<UserConsentResponse>> getActiveConsents(
//            @RequestHeader("X-USER-ID") Long userId
//    ) {
//        return ResponseEntity.ok(userConsentService.getActiveConsents(userId));
//    }
//}
// FILE: domain/consent/controller/UserConsentController.java
package com.ccksy.loan.domain.consent.controller;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ccksy.loan.domain.consent.service.UserConsentService;

/**
 * UserConsentController (v1)
 *
 * 책임:
 * - HTTP 요청/응답 처리(표현 계층)
 * - 인증 컨텍스트에서 userId 해석
 * - Request/Response DTO를 사용하지 않고(폴더 미확정), primitive 기반으로 Service 위임
 *
 * 주의:
 * - Controller는 판단/정책/저장 로직 금지
 * - 실제 응답 포맷 통일(ApiResponse 등)은 common/response에서 처리(프로젝트 정책에 맞춰 교체)
 */
@RestController
@RequestMapping("/api/v1/consent")
public class UserConsentController {

    private final UserConsentService userConsentService;

    public UserConsentController(UserConsentService userConsentService) {
        this.userConsentService = Objects.requireNonNull(userConsentService, "userConsentService");
    }

    /**
     * 동의 여부 조회
     * 예) GET /api/v1/consent/LV3_FINANCIAL
     */
    @GetMapping("/{consentType}")
    public ResponseEntity<Boolean> hasConsent(@PathVariable("consentType") String consentType) {
        Long userId = resolveUserId();
        boolean agreed = userConsentService.hasConsent(userId, normalize(consentType));
        return ResponseEntity.ok(agreed);
    }

    /**
     * 동의 저장/갱신
     * 예) POST /api/v1/consent
     *
     * Request DTO 폴더가 확정되지 않았으므로 v1에서는 Map/primitive를 사용한다.
     *
     * body 예시:
     * {
     *   "consentType": "LV3_FINANCIAL",
     *   "agreed": true
     * }
     */
    @PostMapping
    public ResponseEntity<Void> saveConsent(@RequestBody ConsentBody body) {
        Long userId = resolveUserId();
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
     * 인증 컨텍스트에서 userId 해석 (v1)
     *
     * 규약:
     * - principal이 Number면 그대로 사용
     * - 아니면 principal.toString()이 Long 파싱 가능해야 함
     *
     * NOTE:
     * - 프로젝트에 커스텀 UserDetails가 있으면 여기에서 캐스팅하도록 확장
     */
    private Long resolveUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("Unauthenticated request (no authentication principal).");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof Number) {
            return ((Number) principal).longValue();
        }

        String s = String.valueOf(principal).trim();
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot resolve userId from principal: " + principal);
        }
    }

    /**
     * v1: Request DTO 폴더 미확정에 따른 최소 내부 바디 모델(단일 파일 내)
     * - 외부 패키지로 DTO를 만들지 않고, Controller 내부에서만 사용
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
