package com.pandaterry.gateway.shared.filters;

import com.pandaterry.gateway.shared.service.AgentAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

class AgentAuthenticationFilterTest {

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        AgentAuthService authService = new AgentAuthService();
        webTestClient = WebTestClient.bindToWebHandler(exchange -> Mono.empty())
                .webFilter(new AgentAuthenticationFilter(authService))
                .configureClient()
                .build();
    }

    @Test
    @DisplayName("유효한 시크릿이면 요청이 통과되어야 한다")
    void whenValidSecret_thenPass() {
        webTestClient.get()
                .header("X-AGENT-SECRET", "secret1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("시크릿이 없으면 401을 반환해야 한다")
    void whenNoSecret_thenUnauthorized() {
        webTestClient.get()
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("잘못된 시크릿이면 401을 반환해야 한다")
    void whenInvalidSecret_thenUnauthorized() {
        webTestClient.get()
                .header("X-AGENT-SECRET", "wrong")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
