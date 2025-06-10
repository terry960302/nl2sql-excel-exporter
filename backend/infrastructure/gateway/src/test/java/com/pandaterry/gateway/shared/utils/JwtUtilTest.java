package com.pandaterry.gateway.shared.utils;

import com.pandaterry.msa_contracts.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String secret = "01234567890123456789012345678901"; // 32바이트 이상
    private final long accessExpiredAt = 7200000L;
    private final long refreshExpiredAt = 2592000000L;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(secret, accessExpiredAt, refreshExpiredAt);
    }

    @Test
    @DisplayName(value = "생성된 토큰은 올바른 Payload를 반환해야한다.")
    void validateToken_ValidToken_ReturnsClaims() {
        // 토큰 생성
        String token = Jwts.builder()
                .setSubject("test-user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();

        // 검증 수행
        Jws<Claims> jws = jwtUtil.validateToken(token);

        assertNotNull(jws.getBody());
        assertEquals("test-user", jws.getBody().getSubject());
    }

    @Test
    @DisplayName(value = "유효하지 않은 토큰은 JwtException을 반환해야한다.")
    void validateToken_InvalidToken_ThrowsJwtException() {
        String invalidToken = "invalid.token.value";
        assertThrows(JwtException.class, () -> jwtUtil.validateToken(invalidToken));
    }

    @Test
    @DisplayName(value = "만료된 토큰은 JwtException을 반환해야한다.")
    void validateToken_ExpiredToken_ThrowsJwtException() {
        // 만료된 토큰 생성
        String expiredToken = Jwts.builder()
                .setSubject("expired-user")
                .setIssuedAt(new Date(System.currentTimeMillis() - 120_000))
                .setExpiration(new Date(System.currentTimeMillis() - 60_000))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();

        assertThrows(JwtException.class, () -> jwtUtil.validateToken(expiredToken));
    }
}
