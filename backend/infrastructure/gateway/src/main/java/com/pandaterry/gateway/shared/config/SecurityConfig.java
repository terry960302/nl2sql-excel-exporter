package com.pandaterry.gateway.shared.config;

import com.pandaterry.gateway.shared.filters.AuthHeaderPropagationFilter;
import com.pandaterry.msa_contracts.enums.auth.RoleType;
import com.pandaterry.gateway.shared.filters.AgentAuthenticationFilter;
import com.pandaterry.gateway.shared.filters.JwtAuthenticationFilter;
import com.pandaterry.msa_contracts.constants.RoutePath;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Value("${jwt.secret-key}")
    private String secretKey; // 32바이트 이상의 HMAC SHA 키

    private final String SECRET_KEY_ALGORITHM = "HmacSHA256";

    private static final String WILDCARD_SUFFIX = "/**";

    // 허용 경로
    public static final String[] PUBLIC_PATHS = {
            RoutePath.Common.ACTUATOR + WILDCARD_SUFFIX,
            RoutePath.Auth.SIGNUP + WILDCARD_SUFFIX,
            RoutePath.Auth.LOGIN + WILDCARD_SUFFIX
    };

    // USER, ADMIN 공통 접근
    private static final String[] USER_ADMIN_PATHS = {
            RoutePath.Query.BASE + WILDCARD_SUFFIX,
            RoutePath.Schema.BASE + WILDCARD_SUFFIX,
            RoutePath.Quota.ORG_ME
    };

    // ADMIN 전용
    private static final String[] ADMIN_PATHS = {
            RoutePath.Agent.BASE + WILDCARD_SUFFIX,
            RoutePath.Quota.ORGS + WILDCARD_SUFFIX,
    };

    // AGENT 전용
    private static final String[] AGENT_PATHS = {
            RoutePath.Job.BASE + WILDCARD_SUFFIX,
            RoutePath.Datasource.BASE + WILDCARD_SUFFIX
    };

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            JwtAuthenticationFilter jwtAuthenticationFilter,
                                                            AgentAuthenticationFilter agentAuthenticationFilter) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .addFilterAt(agentAuthenticationFilter, SecurityWebFiltersOrder.FIRST)
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())).authorizeExchange(exchanges -> exchanges

                                .pathMatchers(PUBLIC_PATHS).permitAll()
//                        .pathMatchers(USER_ADMIN_PATHS).hasAnyAuthority(RoleType.USER.getAuthority(),
//                                RoleType.ADMIN.getAuthority())
//                        .pathMatchers(ADMIN_PATHS).hasAuthority(RoleType.ADMIN.getAuthority())
//                        .pathMatchers(AGENT_PATHS).hasAuthority(RoleType.AGENT.getAuthority())
                                .anyExchange().authenticated()
                )
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);

        return http.build();
    }

    @Bean
    @Profile({"local", "test"})
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:1420"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

//    @Bean
//    public ReactiveJwtDecoder reactiveJwtDecoder() {
//        byte[] keyBytes = secretKey.getBytes();
//        SecretKeySpec key = new SecretKeySpec(keyBytes, SECRET_KEY_ALGORITHM);
//        return NimbusReactiveJwtDecoder.withSecretKey(key).build();
//    }

}
