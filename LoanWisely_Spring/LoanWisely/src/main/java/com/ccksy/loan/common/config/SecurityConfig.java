package com.ccksy.loan.common.config;

import com.ccksy.loan.common.security.AdminJwtFilter;
import com.ccksy.loan.common.security.TempApiKeyFilter;
import com.ccksy.loan.common.security.UserJwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           TempApiKeyFilter tempApiKeyFilter,
                                           AdminJwtFilter adminJwtFilter,
                                           UserJwtFilter userJwtFilter) throws Exception {
        // v1 임시 보안: API 키 기반 인증 (추후 JWT로 교체 예정)
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/health/**").permitAll()
                .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                .requestMatchers("/api/admin/auth/login").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers("/api/auth/verify").permitAll()
                .requestMatchers("/api/dev/external-products").permitAll()
                .requestMatchers("/api/dev/external-products/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(adminJwtFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(userJwtFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(tempApiKeyFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
