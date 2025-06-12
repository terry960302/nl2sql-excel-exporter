package com.pandaterry.quota_microservice.application;

import java.util.UUID;

/**
 * Thread-local 컨텍스트에서 요청별 헤더 값을 보관합니다.
 */
public class RequestContext {
    private static final ThreadLocal<RequestContext> HOLDER = new ThreadLocal<>();

    private final UUID orgId;
    private final UUID userId;
    private final UUID agentId;

    private RequestContext(UUID orgId, UUID userId, UUID agentId) {
        this.orgId = orgId;
        this.userId = userId;
        this.agentId = agentId;
    }

    public static void set(UUID orgId, UUID userId, UUID agentId) {
        HOLDER.set(new RequestContext(orgId, userId, agentId));
    }

    public static UUID getOrgId() {
        RequestContext ctx = HOLDER.get();
        return ctx == null ? null : ctx.orgId;
    }

    public static UUID getUserId() {
        RequestContext ctx = HOLDER.get();
        return ctx == null ? null : ctx.userId;
    }

    public static UUID getAgentId() {
        RequestContext ctx = HOLDER.get();
        return ctx == null ? null : ctx.agentId;
    }

    public static void clear() {
        HOLDER.remove();
    }
}
