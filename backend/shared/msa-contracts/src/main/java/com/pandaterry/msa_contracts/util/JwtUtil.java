package com.pandaterry.msa_contracts.util;

import com.pandaterry.msa_contracts.constants.HeaderKeys;
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
        claims.put(HeaderKeys.USER_ID, userId.toString());           // "X-User-Id"
        claims.put(HeaderKeys.ORG_ID, organizationId.toString());    // "X-Organization-Id"
        claims.put(HeaderKeys.ROLES, List.of(RoleType.USER.name())); // "X-Roles"
        // 선택적
        claims.put("email", email);
        claims.put("planName", planName);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
                .signWith(key)
                .setId(UUID.randomUUID().toString())
                .compact();
    }

    public String generateAgentToken(UUID agentId, UUID organizationId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(HeaderKeys.AGENT_ID, agentId.toString());
        claims.put(HeaderKeys.ORG_ID, organizationId.toString());
        claims.put(HeaderKeys.ROLES, List.of("AGENT"));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject("agent::" + agentId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
                .setId(UUID.randomUUID().toString())
                .signWith(key)
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

    public Jws<Claims> validateToken(String token) throws ClaimJwtException {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }

    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return UUID.fromString(claims.getSubject());
    }
}