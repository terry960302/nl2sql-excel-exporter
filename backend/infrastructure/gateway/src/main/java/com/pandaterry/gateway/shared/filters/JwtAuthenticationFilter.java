package com.pandaterry.gateway.shared.filters;

import com.pandaterry.gateway.shared.utils.JwtUtil;
import com.pandaterry.msa_contracts.constants.HeaderKeys;
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

            Claims payload = claims.getPayload();
            String userId = payload.getSubject();
            String organizationId = payload.get("organizationId", String.class);
            @SuppressWarnings("unchecked")
            java.util.List<String> rolesList = (java.util.List<String>) payload.get("roles");
            String roles = rolesList == null ? "" : String.join(",", rolesList);

            ServerHttpRequest newRequest = exchange.getRequest()
                    .mutate()
                    .header(HeaderKeys.USER_ID, userId)
                    .header(HeaderKeys.ORG_ID, organizationId)
                    .header(HeaderKeys.ROLES, roles)
                    .header(HeaderKeys.INTERNAL, "true")
                    .build();

            ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
            return chain.filter(newExchange);
        } catch (JwtException ex) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
