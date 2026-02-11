package com.ccksy.loan.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * 임시 API 키 인증 필터
 * - 로그인/JWT 도입 전까지 사용하는 최소 보안 장치
 * - 성공 시 인증 객체를 주입하여 보호된 엔드포인트 접근 허용
 */
@Component
public class TempApiKeyFilter extends OncePerRequestFilter {

    private static final String HEADER_NAME = "X-API-KEY";

    @Value("${security.temp-api-key}")
    private String expectedKey;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (HttpMethod.OPTIONS.matches(request.getMethod())) return true;
        if (path.startsWith("/health")) return true;
        // API Key는 내부 호출에만 적용
        return !(path.startsWith("/api/internal/") || path.startsWith("/internal/"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String providedKey = request.getHeader(HEADER_NAME);

        if (expectedKey == null || expectedKey.isBlank()) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write("TEMP API KEY가 설정되어 있지 않습니다.");
            return;
        }

        if (providedKey == null || !providedKey.equals(expectedKey)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("유효한 API KEY가 필요합니다.");
            return;
        }

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("temp-api-key", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}
