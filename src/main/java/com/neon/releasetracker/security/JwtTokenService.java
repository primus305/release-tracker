package com.neon.releasetracker.security;

import com.neon.releasetracker.config.SecurityProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "release-tracker.security.enabled", havingValue = "true")
public class JwtTokenService {
    private static final long EXPIRATION_MS = 1000 * 60 * 60 * 8;

    private final SecurityProperties securityProperties;

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(Keys.hmacShaKeyFor(securityProperties.jwtSecret().getBytes()))
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(securityProperties.jwtSecret().getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

}
