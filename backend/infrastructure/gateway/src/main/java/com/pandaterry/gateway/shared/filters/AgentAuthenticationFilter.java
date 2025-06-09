package com.pandaterry.gateway.shared.filters;

import com.pandaterry.gateway.shared.service.AgentAuthService;
import com.pandaterry.gateway.shared.service.AgentInfo;
import com.pandaterry.msa_contracts.constants.HeaderKeys;
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
    private final AgentAuthService agentAuthService;

    public AgentAuthenticationFilter(AgentAuthService agentAuthService) {
        this.agentAuthService = agentAuthService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String secret = exchange.getRequest().getHeaders().getFirst(HeaderKeys.AGENT_SECRET);
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
                    headers.remove(HeaderKeys.AGENT_SECRET);
                    headers.add(HeaderKeys.AGENT_ID, info.agentId());
                    headers.add(HeaderKeys.ORG_ID, info.orgId());
                    headers.add(HeaderKeys.ROLES, "AGENT");
                    headers.add(HeaderKeys.INTERNAL_REQUEST, "true");
                })
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
        return chain.filter(mutatedExchange);
    }
}
