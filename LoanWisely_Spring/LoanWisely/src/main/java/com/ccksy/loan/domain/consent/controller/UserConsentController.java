// FILE: domain/consent/controller/UserConsentController.java
package com.ccksy.loan.domain.consent.controller;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ccksy.loan.common.security.UserIdResolver;
import com.ccksy.loan.domain.consent.service.UserConsentService;

/**
 * UserConsentController (v1)
 *
 * 梨낆엫:
 * - HTTP ?붿껌/?묐떟 泥섎━(?쒗쁽 怨꾩링)
 * - ?몄쬆 而⑦뀓?ㅽ듃?먯꽌 userId ?댁꽍
 * - Request/Response DTO瑜??ъ슜?섏? ?딄퀬(?대뜑 誘명솗??, primitive 湲곕컲?쇰줈 Service ?꾩엫
 *
 * 二쇱쓽:
 * - Controller???먮떒/?뺤콉/???濡쒖쭅 湲덉?
 * - ?ㅼ젣 ?묐떟 ?щ㎎ ?듭씪(ApiResponse ??? common/response?먯꽌 泥섎━(?꾨줈?앺듃 ?뺤콉??留욎떠 援먯껜)
 */
@RestController
@RequestMapping("/api/v1/consent")
public class UserConsentController {

    private final UserConsentService userConsentService;
    private final UserIdResolver userIdResolver;

    public UserConsentController(UserConsentService userConsentService, UserIdResolver userIdResolver) {
        this.userConsentService = Objects.requireNonNull(userConsentService, "userConsentService");
        this.userIdResolver = Objects.requireNonNull(userIdResolver, "userIdResolver");
    }

    /**
     * ?숈쓽 ?щ? 議고쉶
     * ?? GET /api/v1/consent/LV3_FINANCIAL
     */
    @GetMapping("/{consentType}")
    public ResponseEntity<Boolean> hasConsent(@PathVariable("consentType") String consentType) {
        Long userId = userIdResolver.requireUserId();
        boolean agreed = userConsentService.hasConsent(userId, normalize(consentType));
        return ResponseEntity.ok(agreed);
    }

    /**
     * ?숈쓽 ???媛깆떊
     * ?? POST /api/v1/consent
     *
     * Request DTO ?대뜑媛 ?뺤젙?섏? ?딆븯?쇰?濡?v1?먯꽌??Map/primitive瑜??ъ슜?쒕떎.
     *
     * body ?덉떆:
     * {
     *   "consentType": "LV3_FINANCIAL",
     *   "agreed": true
     * }
     */
    @PostMapping
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
     * v1: Request DTO ?대뜑 誘명솗?뺤뿉 ?곕Ⅸ 理쒖냼 ?대? 諛붾뵒 紐⑤뜽(?⑥씪 ?뚯씪 ??
     * - ?몃? ?⑦궎吏濡?DTO瑜?留뚮뱾吏 ?딄퀬, Controller ?대??먯꽌留??ъ슜
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