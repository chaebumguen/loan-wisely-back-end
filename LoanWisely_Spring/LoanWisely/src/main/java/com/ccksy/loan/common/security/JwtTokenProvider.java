package com.ccksy.loan.common.security;

import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

@Component
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "true")
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final String issuer;
    private final String audience;
    private final long clockSkewSeconds;

    public JwtTokenProvider(
            @Value("${app.security.jwt.secret}") String secret,
            @Value("${app.security.jwt.issuer:}") String issuer,
            @Value("${app.security.jwt.audience:}") String audience,
            @Value("${app.security.jwt.clock-skew-seconds:60}") long clockSkewSeconds
    ) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("SECRET_KEY must be configured for HS256.");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.audience = audience;
        this.clockSkewSeconds = clockSkewSeconds;
    }

    public Claims validateAndParse(String token) {
        var parser = Jwts.parser()
                .clockSkewSeconds(clockSkewSeconds)
                .verifyWith(secretKey);

        if (issuer != null && !issuer.isBlank()) {
            parser.requireIssuer(issuer);
        }
        if (audience != null && !audience.isBlank()) {
            parser.requireAudience(audience);
        }

        Jws<Claims> jws = parser.build().parseSignedClaims(token);
        return jws.getPayload();
    }
}
