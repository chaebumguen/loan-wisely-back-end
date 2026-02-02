package com.ccksy.loan.common.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.ccksy.loan.common.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;


/**
 * SecurityConfig (Version 1)
 *
 * 원칙:
 * - 인증/인가 필수(70~71)
 * - 내부(INTERNAL) 엔드포인트는 엄격히 차단
 * - PUBLIC 엔드포인트는 최소 허용(/health 등)
 *
 * NOTE:
 * - OAuth/JWT 발급/검증 인프라가 확정되지 않은 상태에서도 컴파일/기동 가능한 수준으로 구성합니다.
 * - 실제 JWT claim → 권한 매핑은 추후 Converter로 확장 가능합니다.
 */
//@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper objectMapper) throws Exception {

        http
            // API 서버 기본값
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .headers(h -> h.frameOptions(f -> f.sameOrigin())) // H2 콘솔 등을 쓸 경우 대비(운영에서는 재검토)
            .sessionManagement(sm -> sm.sessionCreationPolicy(
                    org.springframework.security.config.http.SessionCreationPolicy.STATELESS
            ))

            // 인증 실패/인가 실패 응답 표준화(내부 정보 노출 방지)
//            .exceptionHandling(ex -> ex
//                .authenticationEntryPoint((request, response, authException) ->
//                        writeAuthError(response, objectMapper, 401, "UNAUTHORIZED", authException))
//                .accessDeniedHandler((request, response, accessDeniedException) ->
//                        writeAccessDenied(response, objectMapper, 403, "FORBIDDEN"))
//            )

            // 경로별 접근 제어
            .authorizeHttpRequests(auth -> auth
                // PUBLIC
                .requestMatchers("/health").permitAll()

                // INTERNAL (운영망/서비스간 호출 전제)
                .requestMatchers("/internal/**").hasAuthority("SCOPE_INTERNAL")

                // PUBLIC metadata 조회
                .requestMatchers("/api/metadata/**").permitAll()

                // ADMIN
                .requestMatchers("/api/admin/**").hasAuthority("SCOPE_OAUTH_ADMIN")

                // USER
                .requestMatchers("/api/users/**").hasAuthority("SCOPE_OAUTH_USER")
                .requestMatchers("/api/recommendations/**").hasAuthority("SCOPE_OAUTH_USER")
                .requestMatchers("/api/events/**").hasAuthority("SCOPE_OAUTH_USER")

                // 그 외는 기본 차단
                .anyRequest().denyAll()
            )

            /**
             * (Version 1) OAuth2 Resource Server(JWT)로 구성합니다.
             * - issuer/jwk-set-uri 등은 application.yml에서 설정해야 합니다.
             */
            .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));

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

        // 메시지는 내부 노출 최소화
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
