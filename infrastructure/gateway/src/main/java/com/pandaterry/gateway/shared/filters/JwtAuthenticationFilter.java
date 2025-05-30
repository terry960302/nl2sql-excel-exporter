package com.pandaterry.gateway.shared.filters;

import com.pandaterry.gateway.shared.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private final String header;
    private final String prefix;

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   @Value("${jwt.header}") String header,
                                   @Value("${jwt.prefix}") String prefix) {
        this.jwtUtil = jwtUtil;
        this.header = header;
        this.prefix = prefix;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        // 헤더에서 토큰 추출
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(header);

        if (!authHeader.startsWith(prefix)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(prefix.length());

        // 3) 토큰 검증
        try {
            Jws<Claims> claims = jwtUtil.validateToken(token);
            // 필요하다면 Claims에서 사용자 정보 꺼내서 SecurityContext에 저장
             exchange.getAttributes().put("claims", claims.getPayload());
        } catch (JwtException ex) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 4) 검증 되었다면 다음 필터 호출
        return chain.filter(exchange);
    }
}
