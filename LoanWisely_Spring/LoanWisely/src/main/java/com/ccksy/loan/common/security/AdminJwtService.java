package com.ccksy.loan.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class AdminJwtService {

    private final String issuer;
    private final long ttlSeconds;
    private final Key signingKey;

    public AdminJwtService(
            @Value("${security.admin-jwt-issuer}") String issuer,
            @Value("${security.admin-jwt-ttl-secs}") long ttlSeconds,
            @Value("${security.admin-jwt-secret}") String secret
    ) {
        this.issuer = issuer;
        this.ttlSeconds = ttlSeconds;
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String issueToken(String adminId, List<String> roles) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(ttlSeconds);
        return Jwts.builder()
                .setSubject(adminId)
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .claim("roles", roles)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public AdminTokenClaims parseToken(String token) {
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token);
        String adminId = claims.getBody().getSubject();
        @SuppressWarnings("unchecked")
        List<String> roles = claims.getBody().get("roles", List.class);
        return new AdminTokenClaims(adminId, roles == null ? List.of() : roles);
    }
}
