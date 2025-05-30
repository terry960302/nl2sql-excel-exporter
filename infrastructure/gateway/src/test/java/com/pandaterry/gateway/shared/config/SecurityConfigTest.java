package com.pandaterry.gateway.shared.config;

import com.pandaterry.gateway.presentation.TestController;
import com.pandaterry.gateway.shared.filters.JwtAuthenticationFilter;
import com.pandaterry.gateway.shared.utils.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@WebFluxTest(controllers = TestController.class)
@Import({SecurityConfig.class, JwtAuthenticationConverter.class, JwtUtil.class,
        JwtAuthenticationFilter.class  // 필터 추가
})
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "jwt.secret=testSecretKey123456789012345678901234567890"
})
class SecurityConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("인증 토큰 없이 요청하면 401 Unauthorized를 반환해야 한다")
    void whenNoToken_thenUnauthorized() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("유효한 토큰으로 요청하면 200 OK를 반환해야 한다")
    void whenValidToken_thenSuccess() {
        String token = Jwts.builder()
                .setSubject("testUser")
                .claim("roles", List.of("USER"))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS256, "testSecretKey123456789012345678901234567890".getBytes())
                .compact();

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/api/test")
                .headers(headers -> headers.setBearerAuth(token))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("test");
    }

    @Test
    @DisplayName("만료된 토큰으로 요청하면 401 Unauthorized를 반환해야 한다")
    void whenExpiredToken_thenUnauthorized() {
        Jwt expiredJwt = Jwt.withTokenValue("expired.token.here")
                .header("alg", "HS256")
                .claim("sub", "testUser")
                .claim("roles", List.of("USER"))
                .issuedAt(Instant.now().minusSeconds(7200))
                .expiresAt(Instant.now().minusSeconds(3600))
                .build();

        webTestClient.mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(expiredJwt))
                .get()
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("USER 역할로 ADMIN 권한이 필요한 요청을 하면 403 Forbidden을 반환해야 한다")
    void whenUserRoleAccessingAdminResource_thenForbidden() {
        Jwt userJwt = Jwt.withTokenValue("user.token.here")
                .header("alg", "HS256")
                .claim("sub", "testUser")
                .claim("roles", List.of("USER"))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        webTestClient.mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(userJwt))
                .get()
                .exchange()
                .expectStatus().isForbidden();
    }
}