package com.pandaterry.api_gateway.shared.converters;

import com.pandaterry.api_gateway.shared.enums.RoleType;
import com.pandaterry.api_gateway.shared.exceptions.ErrorCode;
import com.pandaterry.api_gateway.shared.exceptions.GatewayException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CustomJwtAuthenticationConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    private final String ROLES_KEY = "roles";

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        Instant expiredAt = jwt.getExpiresAt();

        if (expiredAt.isBefore(Instant.now())) {
            throw new GatewayException(ErrorCode.JWT_EXPIRED);
        }

        List<String> roles = jwt.getClaimAsStringList(ROLES_KEY);
        if (roles == null) {
            return Mono.just(new JwtAuthenticationToken(jwt, Collections.EMPTY_LIST));
        }

        return Mono.just(
                new JwtAuthenticationToken(
                        jwt,
                        roles.stream().map(RoleType::from).toList()
                )
        );
    }
}
