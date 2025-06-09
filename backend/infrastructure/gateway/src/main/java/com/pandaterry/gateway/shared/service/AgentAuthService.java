package com.pandaterry.gateway.shared.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AgentAuthService {

    private static final Map<String, AgentInfo> SECRET_STORE = Map.of(
            "secret1", new AgentInfo("agent1", "org1"),
            "secret2", new AgentInfo("agent2", "org2")
    );

    // TODO: 추후엔 디비에 관리하고 있는 시크릿키를 가져와서 있는지 비교해야함.
    public AgentInfo authenticate(String secret) {
        return SECRET_STORE.get(secret);
    }
}
