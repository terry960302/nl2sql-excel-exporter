package com.pandaterry.msa_contracts.util;

import com.pandaterry.msa_contracts.enums.auth.RoleType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long accessTokenExpirationTime;
    private final long refreshTokenExpirationTime;

    public JwtUtil(
            String secretKey,
            long accessTokenExpirationTime,
            long refreshTokenExpirationTime
    ) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
    }

    public String generateAccessToken(UUID userId, String email, UUID organizationId, String planName) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("organizationId", organizationId.toString());
        claims.put("planName", planName);
        claims.put("roles", List.of(RoleType.USER.name()));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
                .signWith(key)
                .setId(UUID.randomUUID().toString())
                .compact();
    }

    public String generateRefreshToken(UUID userId) {
        return this.generateRefreshToken(userId, new Date(System.currentTimeMillis() + refreshTokenExpirationTime));
    }

    public String generateRefreshToken(UUID userId, Date expiredAt) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiredAt)
                .setId(UUID.randomUUID().toString())
                .signWith(key)
                .compact();
    }

    public Jws<Claims> validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
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