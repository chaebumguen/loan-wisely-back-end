// FILE: domain/user/controller/UserProfileController.java
package com.ccksy.loan.domain.user.controller;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ccksy.loan.common.security.UserIdResolver;
import com.ccksy.loan.domain.user.dto.response.UserProfileResponse;
import com.ccksy.loan.domain.user.service.UserProfileService;

/**
 * UserProfileController (v1)
 *
 * 梨낆엫:
 * - HTTP ?붿껌 ?섏떊/?묐떟 諛섑솚 (?쒗쁽 怨꾩링)
 * - ?몄쬆 而⑦뀓?ㅽ듃?먯꽌 userId ?댁꽍
 * - Service濡??꾩엫
 *
 * v1 ?먯튃:
 * - Controller???먮떒/?뺤콉/異붿쿇 濡쒖쭅 湲덉?
 * - Request DTO??"?곌린" ?좎뒪耳?댁뒪?먯꽌留??ъ슜(?꾩옱??議고쉶留??쒓났)
 */
@RestController
@RequestMapping("/api/users/me/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserIdResolver userIdResolver;

    public UserProfileController(UserProfileService userProfileService, UserIdResolver userIdResolver) {
        this.userProfileService = Objects.requireNonNull(userProfileService, "userProfileService");
        this.userIdResolver = Objects.requireNonNull(userIdResolver, "userIdResolver");
    }

    /**
     * ?ъ슜???꾨줈??議고쉶
     */
    @GetMapping
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        Long userId = userIdResolver.requireUserId();
        UserProfileResponse response = userProfileService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }
}
