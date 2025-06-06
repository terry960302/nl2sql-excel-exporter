package com.pandaterry.schema_microservice.integration.support;

import com.pandaterry.schema_microservice.application.RequestContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ContextEchoController {
    @GetMapping("/test/context")
    public Map<String, String> context() {
        Map<String, String> map = new HashMap<>();
        if (RequestContext.getOrgId() != null) {
            map.put("orgId", RequestContext.getOrgId().toString());
        }
        if (RequestContext.getUserId() != null) {
            map.put("userId", RequestContext.getUserId().toString());
        }
        if (RequestContext.getAgentId() != null) {
            map.put("agentId", RequestContext.getAgentId().toString());
        }
        return map;
    }
}
