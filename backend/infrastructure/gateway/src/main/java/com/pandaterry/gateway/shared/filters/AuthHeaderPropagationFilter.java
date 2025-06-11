package com.pandaterry.gateway.shared.filters;

import com.pandaterry.gateway.shared.supports.JwtAuthenticatedPrincipal;
import com.pandaterry.msa_contracts.constants.HeaderKeys;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthHeaderPropagationFilter extends AbstractGatewayFilterFactory<Object> {

    public AuthHeaderPropagationFilter() {
        super(Object.class);
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> ReactiveSecurityContextHolder.getContext()
                .map(context -> {
                    // 인증 정보 조회
                    Authentication authentication = context.getAuthentication();
                    if (authentication == null || !authentication.isAuthenticated()) {
                        return exchange;
                    }

                    // Principal 에 있는 정보 조회
                    Object principalObj = authentication.getPrincipal();
                    if (!(principalObj instanceof JwtAuthenticatedPrincipal principal)) {
                        return exchange;
                    }

                    // Principal 필드 -> Header 변환 -> 전파
                    return exchange.mutate()
                            .request(builder -> builder.headers(headers -> applyHeaders(headers, principal)))
                            .build();
                })
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }

    private void applyHeaders(HttpHeaders headers, JwtAuthenticatedPrincipal principal) {
        headers.set(HeaderKeys.USER_ID, principal.getUserId());
        headers.set(HeaderKeys.ORG_ID, principal.getOrgId());
        headers.set(HeaderKeys.ROLES, String.join(",", principal.getRoles()));

        if (principal.getAgentId() != null) {
            headers.set(HeaderKeys.AGENT_ID, principal.getAgentId());
        }
    }
}
