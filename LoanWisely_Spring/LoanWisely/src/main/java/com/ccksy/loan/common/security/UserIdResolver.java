package com.ccksy.loan.common.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserIdResolver {

    private final List<String> claimKeys;

    public UserIdResolver(
            @Value("${app.security.jwt.user-id-claim:sub}") String primaryClaim,
            @Value("${app.security.jwt.user-id-claim-alt:user_id,uid}") String altClaims
    ) {
        this.claimKeys = new ArrayList<>();
        if (primaryClaim != null && !primaryClaim.isBlank()) {
            this.claimKeys.add(primaryClaim.trim());
        }
        if (altClaims != null && !altClaims.isBlank()) {
            for (String c : altClaims.split(",")) {
                String trimmed = c.trim();
                if (!trimmed.isEmpty()) {
                    this.claimKeys.add(trimmed);
                }
            }
        }
    }

    public Long requireUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return requireUserId(auth);
    }

    public Long requireUserId(Authentication authentication) {
        Objects.requireNonNull(authentication, "authentication must not be null");

        Object principal = authentication.getPrincipal();
        Long id = toLong(principal);
        if (id != null) {
            return id;
        }

        throw new IllegalStateException("Unauthenticated request (cannot resolve userId).");
    }

    private Long toLong(Object v) {
        if (v == null) {
            return null;
        }
        if (v instanceof Number n) {
            return n.longValue();
        }
        String s = v.toString().trim();
        if (s.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(s);
        } catch (Exception ignored) {
            return null;
        }
    }
}
