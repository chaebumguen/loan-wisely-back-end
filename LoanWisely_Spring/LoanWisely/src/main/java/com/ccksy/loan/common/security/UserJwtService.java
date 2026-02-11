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

@Service
public class UserJwtService {

    private final String issuer;
    private final long ttlSeconds;
    private final Key signingKey;

    public UserJwtService(
            @Value("${security.user-jwt-issuer}") String issuer,
            @Value("${security.user-jwt-ttl-secs}") long ttlSeconds,
            @Value("${security.user-jwt-secret}") String secret
    ) {
        this.issuer = issuer;
        this.ttlSeconds = ttlSeconds;
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String issueToken(Long userId, String username) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(ttlSeconds);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .claim("username", username)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public UserTokenClaims parseToken(String token) {
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token);
        String sub = claims.getBody().getSubject();
        String username = claims.getBody().get("username", String.class);
        Long userId = sub == null ? null : Long.parseLong(sub);
        return new UserTokenClaims(userId, username);
    }
}
