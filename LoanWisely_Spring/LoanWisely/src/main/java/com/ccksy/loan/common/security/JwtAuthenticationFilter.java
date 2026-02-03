package com.ccksy.loan.common.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ccksy.loan.common.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "true")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final String scopesClaim;
    private final String scopesClaimAlt;
    private final String rolesClaim;
    private final String rolePrefix;
    private final String userIdClaim;
    private final String userIdClaimAlt;

    public JwtAuthenticationFilter(
            JwtTokenProvider jwtTokenProvider,
            ObjectMapper objectMapper,
            @Value("${app.security.jwt.scopes-claim:scope}") String scopesClaim,
            @Value("${app.security.jwt.scopes-claim-alt:scp}") String scopesClaimAlt,
            @Value("${app.security.jwt.roles-claim:roles}") String rolesClaim,
            @Value("${app.security.jwt.role-prefix:ROLE_}") String rolePrefix,
            @Value("${app.security.jwt.user-id-claim:sub}") String userIdClaim,
            @Value("${app.security.jwt.user-id-claim-alt:user_id,uid}") String userIdClaimAlt
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
        this.scopesClaim = scopesClaim;
        this.scopesClaimAlt = scopesClaimAlt;
        this.rolesClaim = rolesClaim;
        this.rolePrefix = rolePrefix;
        this.userIdClaim = userIdClaim;
        this.userIdClaimAlt = userIdClaimAlt;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader(AUTH_HEADER);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = jwtTokenProvider.validateAndParse(token);
            List<GrantedAuthority> authorities = extractAuthorities(claims);
            Object principal = resolvePrincipal(claims);
            Authentication auth = new UsernamePasswordAuthenticationToken(principal, token, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            writeUnauthorized(response);
        }
    }

    private List<GrantedAuthority> extractAuthorities(Claims claims) {
        List<GrantedAuthority> out = new ArrayList<>();

        addScopes(out, claims.get(scopesClaim));
        if (scopesClaimAlt != null && claims.containsKey(scopesClaimAlt)) {
            addScopes(out, claims.get(scopesClaimAlt));
        }

        Object roles = claims.get(rolesClaim);
        if (roles instanceof String roleString) {
            for (String r : roleString.split(",")) {
                String trimmed = r.trim();
                if (!trimmed.isEmpty()) {
                    out.add(new SimpleGrantedAuthority(normalizeRole(trimmed)));
                }
            }
        } else if (roles instanceof Collection<?> roleList) {
            for (Object r : roleList) {
                if (r != null) {
                    String trimmed = r.toString().trim();
                    if (!trimmed.isEmpty()) {
                        out.add(new SimpleGrantedAuthority(normalizeRole(trimmed)));
                    }
                }
            }
        }

        return out;
    }

    private void addScopes(List<GrantedAuthority> out, Object scopeVal) {
        if (scopeVal == null) {
            return;
        }
        if (scopeVal instanceof String s) {
            for (String part : s.split(" ")) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) {
                    out.add(new SimpleGrantedAuthority("SCOPE_" + trimmed));
                }
            }
        } else if (scopeVal instanceof Collection<?> list) {
            for (Object v : list) {
                if (v != null) {
                    String trimmed = v.toString().trim();
                    if (!trimmed.isEmpty()) {
                        out.add(new SimpleGrantedAuthority("SCOPE_" + trimmed));
                    }
                }
            }
        }
    }

    private Object resolvePrincipal(Claims claims) {
        if (claims.containsKey(userIdClaim)) {
            return claims.get(userIdClaim);
        }
        if (userIdClaimAlt != null) {
            for (String key : userIdClaimAlt.split(",")) {
                String trimmed = key.trim();
                if (!trimmed.isEmpty() && claims.containsKey(trimmed)) {
                    return claims.get(trimmed);
                }
            }
        }
        return claims.getSubject();
    }

    private String normalizeRole(String role) {
        if (role.startsWith(rolePrefix)) {
            return role;
        }
        return rolePrefix + role;
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiResponse<Void> body = ApiResponse.failure("UNAUTHORIZED", "인증이 필요합니다.");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
