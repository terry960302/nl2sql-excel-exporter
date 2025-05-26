package com.pandaterry.auth_microservice.infrastructure.util;

import com.pandaterry.auth_microservice.domain.exception.AuthException;
import com.pandaterry.auth_microservice.domain.exception.ErrorCode;
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
    private final long accessTokenExpirationTime;
    private final long refreshTokenExpirationTime;

    public JwtUtil(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token.expiration-time}") long accessTokenExpirationTime,
            @Value("${jwt.refresh-token.expiration-time}") long refreshTokenExpirationTime
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

    public String generateRefreshToken(UUID userId, Date expiredAt){
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiredAt)
                .setId(UUID.randomUUID().toString())
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
        } catch (ExpiredJwtException e) {
            throw new AuthException(ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthException(ErrorCode.INVALID_TOKEN);
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