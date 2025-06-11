package com.pandaterry.gateway.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.pandaterry.gateway.shared.filters.AgentAuthenticationFilter;
import com.pandaterry.gateway.shared.filters.JwtAuthenticationFilter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.cloud.config.enabled=false",
                "spring.cloud.discovery.enabled=false",
                "jwt.secret-key=dummysecretkeydummysecretkeydummy12",
                "jwt.access-token.expiration-time=3600",
                "jwt.refresh-token.expiration-time=7200"
        })
@AutoConfigureWebTestClient
@TestPropertySource(properties = {
        "jwt.header=Authorization",
        "jwt.prefix=Bearer "
})
class GatewayRoutingTest {

    private static final WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        wireMockServer.start();
        wireMockServer.stubFor(WireMock.get("/api/v1/auth/login/mock")
                .willReturn(WireMock.aResponse().withStatus(200).withBody("hello")));

        registry.add("spring.cloud.gateway.routes[0].id", () -> "login-route");
        registry.add("spring.cloud.gateway.routes[0].uri", wireMockServer::baseUrl);
        registry.add("spring.cloud.gateway.routes[0].predicates[0]", () -> "Path=/api/v1/auth/login/**");
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    AgentAuthenticationFilter agentAuthenticationFilter;

    @BeforeEach
    void setupFilters() {
        when(jwtAuthenticationFilter.filter(any(), any()))
                .thenAnswer(invocation -> {
                    ServerWebExchange exchange = invocation.getArgument(0);
                    WebFilterChain chain = invocation.getArgument(1);
                    return chain.filter(exchange);
                });
        when(agentAuthenticationFilter.filter(any(), any()))
                .thenAnswer(invocation -> {
                    ServerWebExchange exchange = invocation.getArgument(0);
                    WebFilterChain chain = invocation.getArgument(1);
                    return chain.filter(exchange);
                });
    }

    @Autowired
    WebTestClient webTestClient;

    @Test
    void routeRequestToWireMock() {
        webTestClient.get().uri("/api/v1/auth/login/mock")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("hello");
    }
}
