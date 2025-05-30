package com.pandaterry.gateway.shared.converters;

import com.pandaterry.gateway.shared.enums.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomJwtAuthenticationConverterTest {

    private CustomJwtAuthenticationConverter jwtAuthenticationConverter;
    private Jwt validJwt;
    private Jwt expiredJwt;
    private Jwt jwtWithoutRoles;

    @BeforeEach
    void setUp() {
        jwtAuthenticationConverter = new CustomJwtAuthenticationConverter();

        // 유효한 JWT 생성
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", List.of("USER", "ADMIN"));
        
        validJwt = Jwt.withTokenValue("valid.token.here")
                .header("alg", "HS256")
                .claim("sub", "testUser")
                .claim("roles", List.of(RoleType.USER, RoleType.ADMIN))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        // 만료된 JWT 생성
        expiredJwt = Jwt.withTokenValue("expired.token.here")
                .header("alg", "HS256")
                .claim("sub", "testUser")
                .claim("roles", List.of(RoleType.USER))
                .issuedAt(Instant.now().minusSeconds(7200))
                .expiresAt(Instant.now().minusSeconds(3600))
                .build();

        // 역할 정보가 없는 JWT 생성
        jwtWithoutRoles = Jwt.withTokenValue("no.roles.token.here")
                .header("alg", "HS256")
                .claim("sub", "testUser")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }


    @Test
    @DisplayName("유효한 토큰이 주어졌을 때 Authentication 객체가 정상적으로 생성되어야 한다")
    void whenValidToken_thenAuthenticationCreated() {
        // when
        Authentication authentication = jwtAuthenticationConverter.convert(validJwt).block();

        // then
        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo("testUser");
        assertThat(authentication.getAuthorities())
                .hasSize(2);
    }

    @Test
    @DisplayName("만료된 토큰이 주어졌을 때 JWT 만료 예외가 발생해야 한다")
    void whenExpiredToken_thenThrowException() {
        // when & then
        assertThatThrownBy(() -> jwtAuthenticationConverter.convert(expiredJwt))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("JWT expired");
    }

    @Test
    @DisplayName("역할 정보가 없는 토큰이 주어졌을 때 빈 권한 목록을 가진 Authentication이 생성되어야 한다")
    void whenTokenWithoutRoles_thenAuthenticationWithNoAuthorities() {
        // when
        Authentication authentication = jwtAuthenticationConverter.convert(jwtWithoutRoles).block();

        // then
        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo("testUser");
        assertThat(authentication.getAuthorities()).isEmpty();
    }
}