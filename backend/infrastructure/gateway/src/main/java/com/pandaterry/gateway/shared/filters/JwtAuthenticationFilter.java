package com.pandaterry.gateway.shared.filters;

import com.pandaterry.gateway.shared.config.SecurityConfig;
import com.pandaterry.gateway.shared.supports.AuthenticationFactory;
import com.pandaterry.gateway.shared.supports.JwtTokenExtractor;
import com.pandaterry.gateway.shared.supports.PublicPathChecker;
import com.pandaterry.msa_contracts.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private final String header;
    private final String prefix;
    private final PublicPathChecker publicPathChecker;
    private final JwtTokenExtractor jwtTokenExtractor;
    private final AuthenticationFactory authenticationFactory;

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   @Value("${jwt.header}") String header,
                                   @Value("${jwt.prefix}") String prefix) {
        this.jwtUtil = jwtUtil;
        this.header = header;
        this.prefix = prefix;
        this.jwtTokenExtractor = new JwtTokenExtractor(header, prefix);
        this.publicPathChecker = new PublicPathChecker(List.of(SecurityConfig.PUBLIC_PATHS));
        this.authenticationFactory = new AuthenticationFactory();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // 인증을 무시할 경로인지 확인
        if (publicPathChecker.isPublic(path)) {
            return chain.filter(exchange);
        }

        return jwtTokenExtractor.extract(exchange)
                .map(token -> {
                    // 토큰 검증
                    Jws<Claims> claims = jwtUtil.validateToken(token);
                    exchange.getAttributes().put("claims", claims.getBody());

                    // Authentication 객체 생성
                    return authenticationFactory.create(claims);
                })
                .map(auth -> chain.filter(exchange) // authenticationa 을 SecurityContext 에 저장
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
                .orElseGet(() -> {
                    // 그외는 에러로 반환
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                });
    }
}
