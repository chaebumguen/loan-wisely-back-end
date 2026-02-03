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
 * ?먯튃:
 * - ?몄쬆/?멸? ?꾩닔(70~71)
 * - ?대?(INTERNAL) ?붾뱶?ъ씤?몃뒈 ?꾧꺽??李⑤떒
 * - PUBLIC ?붾뱶?ъ씤?몃뒈 理쒖냼 ?덉슜(/health ??
 *
 * NOTE:
 * - OAuth/JWT 諛쒓툒/寃利??명봽?쇨? ?뺤젙?섏? ?딆? ?곹깭?먯꽌??而댄뙆??湲곕룞 媛?ν븳 ?섏??쇰줈 援ъ꽦?⑸땲??
 * - ?ㅼ젣 JWT claim ??沅뚰븳 留ㅽ븨? 異뷀썑 Converter濡??뺤옣 媛?ν빀?덈떎.
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
            // API ?쒕쾭 湲곕낯媛?
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .headers(h -> h.frameOptions(f -> f.sameOrigin())) // H2 肄섏넄 ?깆쓣 ??寃쎌슦 ?鍮??댁쁺?먯꽌???ш???
            .sessionManagement(sm -> sm.sessionCreationPolicy(
                    org.springframework.security.config.http.SessionCreationPolicy.STATELESS
            ))

            // ?몄쬆 ?ㅽ뙣/?멸? ?ㅽ뙣 ?묐떟 ?쒖????대? ?뺣낫 ?몄텧 諛⑹?)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                        writeAuthError(response, objectMapper, 401, "UNAUTHORIZED", authException))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                        writeAccessDenied(response, objectMapper, 403, "FORBIDDEN"))
            )

            // 寃쎈줈蹂??묎렐 ?쒖뼱
            .authorizeHttpRequests(auth -> auth
                // PUBLIC
                .requestMatchers("/health").permitAll()
                .requestMatchers("/actuator/health/**").permitAll()
                .requestMatchers("/actuator/info").permitAll()

                // INTERNAL (?댁쁺留??쒕퉬?ㅺ컙 ?몄텧 ?꾩젣)
                .requestMatchers("/internal/**").hasAuthority("SCOPE_INTERNAL")

                // PUBLIC metadata 議고쉶
                .requestMatchers("/api/metadata/**").permitAll()

                // PRODUCTS (READ: public, WRITE: admin)
                .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/products", "/api/products/**").hasAuthority("SCOPE_OAUTH_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAuthority("SCOPE_OAUTH_ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/products/**").hasAuthority("SCOPE_OAUTH_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAuthority("SCOPE_OAUTH_ADMIN")

                // USER PROFILE / CONSENT (USER)
                .requestMatchers("/api/v1/user/profile/**").hasAuthority("SCOPE_OAUTH_USER")
                .requestMatchers("/api/v1/consent/**").hasAuthority("SCOPE_OAUTH_USER")

                // RECOMMENDATIONS (USER)
                .requestMatchers("/api/recommendations/**").hasAuthority("SCOPE_OAUTH_USER")

                // ADMIN
                .requestMatchers("/api/admin/**").hasAuthority("SCOPE_OAUTH_ADMIN")

                // LEGACY FALLBACKS (tighten later)
                .requestMatchers("/api/users/**").hasAuthority("SCOPE_OAUTH_USER")
                .requestMatchers("/api/events/**").hasAuthority("SCOPE_OAUTH_USER")

                // 洹??몃뒈 湲곕낯 李⑤떒
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

        // 硫붿떆吏???대? ?몄텧 理쒖냼??
        ApiResponse<Void> body = ApiResponse.failure(code, "?몄쬆???꾩슂?⑸땲??");
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

        ApiResponse<Void> body = ApiResponse.failure(code, "?묎렐 沅뚰븳???놁뒿?덈떎.");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
