package com.pandaterry.gateway.shared.filters;

import com.pandaterry.gateway.shared.config.SecurityConfig;
import com.pandaterry.gateway.shared.exceptions.ErrorCode;
import com.pandaterry.gateway.shared.exceptions.GatewayException;
import com.pandaterry.gateway.shared.utils.AuthorityMapper;
import com.pandaterry.msa_contracts.enums.auth.RoleType;
import com.pandaterry.msa_contracts.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        String path = exchange.getRequest().getPath().value();

        // 인증을 무시할 경로인지 확인
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        // 헤더에서 토큰 추인
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(header);

        if (!Objects.requireNonNull(authHeader).startsWith(prefix)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(prefix.length())
                .replaceAll(" ", "");

        // 3) 토큰 검증
        try {
            Jws<Claims> claims = jwtUtil.validateToken(token);
            // 필요하다면 Claims에서 사용자 정보 꺼내서 SecurityContext에 저장
            exchange.getAttributes().put("claims", claims.getBody());

            Authentication authentication = createAuthentication(claims);

            // 4) 검증 되었다면 다음 필터 호출
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

        } catch (JwtException ex) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isPublicPath(String path) {
        return Arrays.stream(SecurityConfig.PUBLIC_PATHS)
                .map(publicPath -> new AntPathMatcher().match(publicPath, path))
                .anyMatch(e -> e.equals(true));
    }

    private Authentication createAuthentication(Jws<Claims> claims) {
        String username = claims.getBody().getSubject();
        List<String> roles = claims.getBody().get("roles", List.class);
        if(roles == null){
            throw new GatewayException(ErrorCode.ROLES_SHOULD_NOT_EMPTY);
        }
        List<GrantedAuthority> authorities = roles.stream()
                .map(RoleType::from)
                .map(AuthorityMapper::toGranted)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(
                username, null, authorities
        );
    }
}
