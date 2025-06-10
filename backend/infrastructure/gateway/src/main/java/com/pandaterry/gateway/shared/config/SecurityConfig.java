package com.pandaterry.gateway.shared.config;

import com.pandaterry.msa_contracts.enums.auth.RoleType;
import com.pandaterry.gateway.shared.filters.AgentAuthenticationFilter;
import com.pandaterry.gateway.shared.filters.JwtAuthenticationFilter;
import com.pandaterry.msa_contracts.constants.RoutePath;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Value("${jwt.secret-key}")
    private String secretKey; // 32바이트 이상의 HMAC SHA 키

    private final String SECRET_KEY_ALGORITHM = "HmacSHA256";

    // 허용 경로
    public static final String[] PUBLIC_PATHS = {
            RoutePath.Common.ACTUATOR + "/**",
            RoutePath.Auth.SIGNUP + "/**",
            RoutePath.Auth.LOGIN + "/**"
    };

    // USER, ADMIN 공통 접근
    private static final String[] USER_ADMIN_PATHS = {
            RoutePath.Query.BASE + "/**",
            RoutePath.Schema.BASE + "/**",
            RoutePath.Quota.ORG_ME
    };

    // ADMIN 전용
    private static final String[] ADMIN_PATHS = {
            RoutePath.Agent.BASE + "/**",
            RoutePath.Quota.ORGS + "/**"
    };

    // AGENT 전용
    private static final String[] AGENT_PATHS = {
            RoutePath.Job.BASE + "/**",
            RoutePath.Datasource.BASE + "/**"
    };

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            JwtAuthenticationFilter jwtAuthenticationFilter,
                                                            AgentAuthenticationFilter agentAuthenticationFilter) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .addFilterAt(agentAuthenticationFilter, SecurityWebFiltersOrder.FIRST)
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(PUBLIC_PATHS).permitAll()
                        .pathMatchers(USER_ADMIN_PATHS).hasAnyAuthority(RoleType.USER.getAuthority(),
                                RoleType.ADMIN.getAuthority())
                        .pathMatchers(ADMIN_PATHS).hasAuthority(RoleType.ADMIN.getAuthority())
                        .pathMatchers(AGENT_PATHS).hasAuthority(RoleType.AGENT.getAuthority())
                        .anyExchange().authenticated()
                );
//                .oauth2ResourceServer(oauth2 -> oauth2
//                        .jwt(jwt -> jwt
//                                .jwtDecoder(reactiveJwtDecoder())
//                                .jwtAuthenticationConverter(new CustomJwtAuthenticationConverter())
//                        )
//                );

        return http.build();
    }

//    @Bean
//    public ReactiveJwtDecoder reactiveJwtDecoder() {
//        byte[] keyBytes = secretKey.getBytes();
//        SecretKeySpec key = new SecretKeySpec(keyBytes, SECRET_KEY_ALGORITHM);
//        return NimbusReactiveJwtDecoder.withSecretKey(key).build();
//    }

}
