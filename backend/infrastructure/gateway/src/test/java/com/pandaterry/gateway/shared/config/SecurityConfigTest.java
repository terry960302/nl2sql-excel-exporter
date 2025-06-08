package com.pandaterry.gateway.shared.config;

import com.pandaterry.gateway.presentation.TestController;
import com.pandaterry.gateway.shared.filters.AgentAuthenticationFilter;
import com.pandaterry.gateway.shared.filters.JwtAuthenticationFilter;
import com.pandaterry.gateway.shared.service.AgentAuthService;
import com.pandaterry.gateway.shared.utils.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.junit.jupiter.api.BeforeEach;
import com.pandaterry.gateway.shared.filters.JwtAuthenticationFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@WebFluxTest(controllers = TestController.class)
@Import({SecurityConfig.class, JwtAuthenticationConverter.class, JwtUtil.class,
        JwtAuthenticationFilter.class,
        AgentAuthenticationFilter.class,
        AgentAuthService.class
})
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "jwt.secret=testSecretKey123456789012345678901234567890"
})
class SecurityConfigTest {

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        when(jwtAuthenticationFilter.filter(any(), any()))
                .thenAnswer(invocation -> {
                    var exchange = invocation.getArgument(0, org.springframework.web.server.ServerWebExchange.class);
                    var chain = invocation.getArgument(1, org.springframework.web.server.WebFilterChain.class);
                    return chain.filter(exchange);
                });
    }

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("인증 토큰 없이 요청하면 401을 반환한다")
    void whenNoToken_thenUnauthorized() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("USER 권한으로 /queries 접근 시 성공해야 한다")
    void userRoleAccessQueries_thenSuccess() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                .get().uri("/queries/test")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("queries");
    }

    @Test
    @DisplayName("USER 권한으로 /agents 접근 시 Forbidden이 발생해야 한다")
    void userRoleAccessAgents_thenForbidden() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                .get().uri("/agents/test")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("AGENT 권한으로 /jobs 접근 시 성공해야 한다")
    void agentAuthorityAccessJobs_thenSuccess() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("AGENT")))
                .get().uri("/jobs/test")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("jobs");
    }
}
