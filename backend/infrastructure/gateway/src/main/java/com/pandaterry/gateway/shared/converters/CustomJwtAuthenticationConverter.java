package com.pandaterry.gateway.shared.converters;

import com.pandaterry.gateway.shared.utils.AuthorityMapper;
import com.pandaterry.msa_contracts.enums.auth.RoleType;
import com.pandaterry.gateway.shared.exceptions.ErrorCode;
import com.pandaterry.gateway.shared.exceptions.GatewayException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CustomJwtAuthenticationConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    private final String ROLES_KEY = "roles";

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        Instant expiredAt = jwt.getExpiresAt();

        if (Objects.requireNonNull(expiredAt).isBefore(Instant.now())) {
            throw new GatewayException(ErrorCode.JWT_EXPIRED);
        }

        List<String> roles = jwt.getClaimAsStringList(ROLES_KEY);
        if (roles == null) {
            return Mono.just(new JwtAuthenticationToken(jwt, Collections.EMPTY_LIST));
        }

        return Mono.just(
                new JwtAuthenticationToken(
                        jwt,
                        AuthorityMapper.toGranted(roles.stream().map(RoleType::from).toList())
                )
        );
    }
}
