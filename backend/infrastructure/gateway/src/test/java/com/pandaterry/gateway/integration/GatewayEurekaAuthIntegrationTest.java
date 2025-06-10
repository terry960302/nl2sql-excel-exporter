import com.pandaterry.eureka_server.EurekaServerApplication;
import com.pandaterry.auth_microservice.AuthMicroserviceApplication;
import com.pandaterry.gateway.GatewayApplication;
import com.pandaterry.msa_contracts.dto.auth.request.LoginRequest;
import com.pandaterry.msa_contracts.dto.auth.request.SignupRequest;
import com.pandaterry.msa_contracts.dto.auth.response.TokenResponse;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GatewayEurekaAuthIntegrationTest {
    private ConfigurableApplicationContext eureka;
    private ConfigurableApplicationContext authService;
    private ConfigurableApplicationContext gateway;

    private int eurekaPort;
    private int gatewayPort;

    @BeforeAll
    void setUp() {
        eureka = new SpringApplicationBuilder(EurekaServerApplication.class)
                .properties(
                        "server.port=0",
                        "spring.cloud.config.enabled=false",
                        "eureka.client.register-with-eureka=false",
                        "eureka.client.fetch-registry=false"
                ).run();
        eurekaPort = ((ReactiveWebServerApplicationContext) eureka).getWebServer().getPort();

        authService = new SpringApplicationBuilder(AuthMicroserviceApplication.class)
                .properties(
                        "server.port=0",
                        "spring.profiles.active=test",
                        "spring.cloud.config.enabled=false",
                        "eureka.client.serviceUrl.defaultZone=http://localhost:" + eurekaPort + "/eureka"
                ).run();
        int authPort = ((ReactiveWebServerApplicationContext) authService).getWebServer().getPort();

        System.setProperty("spring.main.web-application-type", "reactive");

        gateway = new SpringApplicationBuilder(GatewayApplication.class)
                .web(org.springframework.boot.WebApplicationType.REACTIVE)
                .properties(
                        "server.port=0",
                        "spring.cloud.config.enabled=false",
                        "eureka.client.serviceUrl.defaultZone=http://localhost:" + eurekaPort + "/eureka",
                        "auth-service.base-url=http://localhost:" + authPort + "/api"
                ).run();
        gatewayPort = ((ReactiveWebServerApplicationContext) gateway).getWebServer().getPort();
    }

    @AfterAll
    void tearDown() {
        gateway.close();
        authService.close();
        eureka.close();
    }

    @Test
    @DisplayName("게이트웨이와 인증 서비스가 유레카에 등록되고 로그인 요청이 성공해야 한다")
    void eurekaIntegrationAndLogin() {
        WebClient eurekaClient = WebClient.builder()
                .baseUrl("http://localhost:" + eurekaPort + "/eureka")
                .build();

        Awaitility.await().atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    String body = eurekaClient.get().uri("/apps").retrieve().bodyToMono(String.class).block();
                    assertThat(body).containsIgnoringCase("GATEWAY").containsIgnoringCase("AUTH-SERVICE");
                });

        WebClient client = WebClient.builder()
                .baseUrl("http://localhost:" + gatewayPort + "/api/v1")
                .build();

        SignupRequest signupRequest = SignupRequest.builder()
                .name("tester")
                .email("tester@example.com")
                .password("ValidPass123!")
                .build();
        client.post().uri("/auth/signup")
                .bodyValue(signupRequest)
                .retrieve()
                .toBodilessEntity()
                .block();

        LoginRequest loginRequest = LoginRequest.builder()
                .email("tester@example.com")
                .password("ValidPass123!")
                .build();
        TokenResponse token = client.post().uri("/auth/login")
                .bodyValue(loginRequest)
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();

        assertThat(token.getAccessToken()).isNotBlank();
    }
}
