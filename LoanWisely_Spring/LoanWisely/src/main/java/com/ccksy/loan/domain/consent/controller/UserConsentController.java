package com.ccksy.loan.domain.consent.controller;

import com.ccksy.loan.domain.consent.dto.request.UserConsentRequest;
import com.ccksy.loan.domain.consent.dto.response.UserConsentResponse;
import com.ccksy.loan.domain.consent.service.UserConsentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * (Facade) 입력 레벨별 동의 관리 진입점
 * API 명세(발췌): POST/GET /api/users/me/consents:contentReference[oaicite:11]{index=11}
 */
@RestController
@RequestMapping("/api/users/me/consents")
public class UserConsentController {

    private final UserConsentService userConsentService;

    public UserConsentController(UserConsentService userConsentService) {
        this.userConsentService = userConsentService;
    }

    /**
     * 동의 생성
     * - 목적태그(purposeTags)는 DB에 JSON으로 넣지 않고 파일 경로로 저장(요청사항 반영)
     */
    @PostMapping
    public ResponseEntity<UserConsentResponse> createConsent(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestBody UserConsentRequest request
    ) {
        UserConsentResponse created = userConsentService.createConsent(userId, request);
        return ResponseEntity.ok(created);
    }

    /**
     * 현재 유효 동의 조회(만료/비활성 제외)
     */
    @GetMapping
    public ResponseEntity<List<UserConsentResponse>> getActiveConsents(
            @RequestHeader("X-USER-ID") Long userId
    ) {
        return ResponseEntity.ok(userConsentService.getActiveConsents(userId));
    }
}
