package com.pandaterry.gateway.shared.supports;

import org.springframework.web.server.ServerWebExchange;

import java.util.Optional;

public class JwtTokenExtractor {
    private final String headerName;
    private final String prefix;

    public JwtTokenExtractor(String headerName, String prefix) {
        this.headerName = headerName;
        this.prefix = prefix;
    }

    public Optional<String> extract(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(headerName);
        if (authHeader != null && authHeader.startsWith(prefix)) {
            return Optional.of(authHeader.substring(prefix.length()).trim());
        }
        return Optional.empty();
    }
}
