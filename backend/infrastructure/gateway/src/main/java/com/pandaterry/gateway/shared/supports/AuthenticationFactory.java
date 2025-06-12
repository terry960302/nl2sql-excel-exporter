package com.pandaterry.gateway.shared.supports;

import com.pandaterry.gateway.shared.exceptions.ErrorCode;
import com.pandaterry.gateway.shared.exceptions.GatewayException;
import com.pandaterry.gateway.shared.utils.AuthorityMapper;
import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.enums.auth.RoleType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

public class AuthenticationFactory {
    public Authentication create(Jws<Claims> claims) {
        String userId = claims.getBody().get(HeaderKeys.USER_ID, String.class);
        String orgId = claims.getBody().get(HeaderKeys.ORG_ID, String.class);
        List<String> roles = claims.getBody().get(HeaderKeys.ROLES, List.class);
        String agentId = claims.getBody().get(HeaderKeys.AGENT_ID, String.class); // nullable
        String email = claims.getBody().get("email", String.class);
        String planName = claims.getBody().get("planName", String.class);


        if (roles == null || roles.isEmpty()) {
            throw new GatewayException(ErrorCode.ROLES_SHOULD_NOT_EMPTY);
        }

        List<GrantedAuthority> authorities = roles.stream()
                .map(RoleType::from)
                .map(AuthorityMapper::toGranted)
                .collect(Collectors.toList());
        JwtAuthenticatedPrincipal principal = new JwtAuthenticatedPrincipal(
                userId, orgId, roles, agentId
        );
        return new UsernamePasswordAuthenticationToken(principal, claims.getSignature(), authorities);
    }
}
