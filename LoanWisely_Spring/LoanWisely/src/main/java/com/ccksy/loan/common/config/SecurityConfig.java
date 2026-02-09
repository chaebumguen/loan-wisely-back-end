package com.ccksy.loan.common.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ccksy.loan.common.response.ApiResponse;
import com.ccksy.loan.common.security.JwtAuthenticationFilter;
import com.ccksy.loan.common.security.JwtUserContextFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;


/**
 * SecurityConfig (Version 1)
 *
 * 목적:
 * - 인증/인가 기본 정책
 * - 내부(INTERNAL) 엔드포인트는 차단
 * - PUBLIC 엔드포인트는 최소 허용(/health 등)
 *
 * NOTE:
 * - OAuth/JWT 발급/검증 흐름이 확정되지 않아 최소한의 필터 체인으로 구성합니다.
 * - 실제 JWT claim 기반 권한 매핑은 추후 Converter로 확장합니다.
 */
@Configuration
@EnableMethodSecurity
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "true")
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ObjectMapper objectMapper,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            JwtUserContextFilter jwtUserContextFilter
    ) throws Exception {

        http
            // API 서버 기본값
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .headers(h -> h.frameOptions(f -> f.sameOrigin())) // H2 콘솔 접근 시에만 sameOrigin 사용
            .sessionManagement(sm -> sm.sessionCreationPolicy(
                    org.springframework.security.config.http.SessionCreationPolicy.STATELESS
            ))

            // 인증 실패/인가 실패 응답 표준화 (내부 정보 노출 방지)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                        writeAuthError(response, objectMapper, 401, "UNAUTHORIZED", authException))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                        writeAccessDenied(response, objectMapper, 403, "FORBIDDEN"))
            )

            // 경로별 권한 제어
            .authorizeHttpRequests(auth -> auth
                // PUBLIC
                .requestMatchers("/health").permitAll()
                .requestMatchers("/actuator/health/**").permitAll()
                .requestMatchers("/actuator/info").permitAll()

                // INTERNAL (내부 서비스 간 호출 전용)
                .requestMatchers("/internal/**").hasAuthority("SCOPE_INTERNAL")

                // PUBLIC metadata 조회
                .requestMatchers("/api/metadata/**").permitAll()

                // PRODUCTS (READ: public, WRITE: admin)
                .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/products", "/api/products/**").hasAuthority("SCOPE_OAUTH_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAuthority("SCOPE_OAUTH_ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/products/**").hasAuthority("SCOPE_OAUTH_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAuthority("SCOPE_OAUTH_ADMIN")

                // USER PROFILE / CONSENT (USER)
                .requestMatchers("/api/users/me/profile/**").hasAuthority("SCOPE_OAUTH_USER")
                .requestMatchers("/api/users/me/consents/**").hasAuthority("SCOPE_OAUTH_USER")
                // legacy consent GET (kept while consentType design is on hold)
                .requestMatchers("/api/v1/consent/**").hasAuthority("SCOPE_OAUTH_USER")

                // RECOMMENDATIONS (USER)
                .requestMatchers("/api/recommendations/**").hasAuthority("SCOPE_OAUTH_USER")

                // ADMIN
                .requestMatchers("/api/admin/**").hasAuthority("SCOPE_OAUTH_ADMIN")

                // LEGACY FALLBACKS (tighten later)
                .requestMatchers("/api/users/**").hasAuthority("SCOPE_OAUTH_USER")
                .requestMatchers("/api/events/**").hasAuthority("SCOPE_OAUTH_USER")

                // 그 외는 기본 차단
                .anyRequest().denyAll()
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(jwtUserContextFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    private static void writeAuthError(
            HttpServletResponse response,
            ObjectMapper objectMapper,
            int status,
            String code,
            AuthenticationException ex
    ) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 메시지에 내부 정보 노출 최소화
        ApiResponse<Void> body = ApiResponse.failure(code, "인증이 필요합니다.");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    private static void writeAccessDenied(
            HttpServletResponse response,
            ObjectMapper objectMapper,
            int status,
            String code
    ) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<Void> body = ApiResponse.failure(code, "접근 권한이 없습니다.");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
