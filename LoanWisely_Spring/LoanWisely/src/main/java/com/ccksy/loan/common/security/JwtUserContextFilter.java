package com.ccksy.loan.common.security;

import java.io.IOException;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtUserContextFilter extends OncePerRequestFilter {

    private static final String ATTR_USER_ID = "userId";
    private static final String MDC_USER_ID = "userId";

    private final UserIdResolver userIdResolver;

    public JwtUserContextFilter(UserIdResolver userIdResolver) {
        this.userIdResolver = userIdResolver;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            try {
                Long userId = userIdResolver.requireUserId(auth);
                request.setAttribute(ATTR_USER_ID, userId);
                MDC.put(MDC_USER_ID, String.valueOf(userId));
            } catch (Exception ignored) {
                // no-op: keep request unauthenticated semantics
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_USER_ID);
        }
    }
}
