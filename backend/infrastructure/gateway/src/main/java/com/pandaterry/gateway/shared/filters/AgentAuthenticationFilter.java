package com.pandaterry.gateway.shared.filters;

import com.pandaterry.gateway.shared.service.AgentAuthService;
import com.pandaterry.gateway.shared.service.AgentInfo;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AgentAuthenticationFilter implements WebFilter {

    private static final String SECRET_HEADER = "X-AGENT-SECRET";
    private final AgentAuthService agentAuthService;

    public AgentAuthenticationFilter(AgentAuthService agentAuthService) {
        this.agentAuthService = agentAuthService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String secret = exchange.getRequest().getHeaders().getFirst(SECRET_HEADER);
        if (secret == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        AgentInfo info = agentAuthService.authenticate(secret);
        if (info == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        var mutatedRequest = exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.remove(SECRET_HEADER);
                    headers.add("X-Agent-Id", info.agentId());
                    headers.add("X-Org-Id", info.orgId());
                    headers.add("X-Roles", "AGENT");
                    headers.add("X-Internal-Request", "true");
                })
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
        return chain.filter(mutatedExchange);
    }
}
