package com.pandaterry.gateway.shared.supports;

import com.pandaterry.msa_contracts.enums.auth.RoleType;
import lombok.Getter;

import java.util.List;

@Getter
public class JwtAuthenticatedPrincipal {
    private final String userId;
    private final String orgId;
    private final List<String> roles;
    private final String agentId; // optional

    public JwtAuthenticatedPrincipal(String userId, String orgId, List<String> roles, String agentId) {
        this.userId = userId;
        this.orgId = orgId;
        this.roles = roles;
        this.agentId = agentId;
    }
}
