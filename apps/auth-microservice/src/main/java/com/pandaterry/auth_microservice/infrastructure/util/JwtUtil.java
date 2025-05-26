package com.pandaterry.auth_microservice.infrastructure.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long accessTokenValidityInMinutes;
    private final long refreshTokenValidityInDays;

    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-validity-in-minutes}") long accessTokenValidityInMinutes,
            @Value("${jwt.refresh-token-validity-in-days}") long refreshTokenValidityInDays) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityInMinutes = accessTokenValidityInMinutes;
        this.refreshTokenValidityInDays = refreshTokenValidityInDays;
    }

    public String generateAccessToken(UUID userId, String email, UUID organizationId, String planName) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("organizationId", organizationId.toString());
        claims.put("planName", planName);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidityInMinutes * 60 * 1000))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(UUID userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidityInDays * 24 * 60 * 60 * 1000))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return UUID.fromString(claims.getSubject());
    }
}