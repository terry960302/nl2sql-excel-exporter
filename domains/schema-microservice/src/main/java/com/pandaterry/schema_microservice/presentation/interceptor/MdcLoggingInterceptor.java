package com.pandaterry.schema_microservice.presentation.interceptor;

import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.schema_microservice.application.RequestContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * 요청 헤더의 조직, 사용자, 에이전트 정보를 MDC와 RequestContext에 저장합니다.
 */
@Component
public class MdcLoggingInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String org = request.getHeader(HeaderKeys.ORG_ID);
        String user = request.getHeader(HeaderKeys.USER_ID);
        String agent = request.getHeader(HeaderKeys.AGENT_ID);

        if (org != null) {
            MDC.put(HeaderKeys.ORG_ID, org);
        }
        if (user != null) {
            MDC.put(HeaderKeys.USER_ID, user);
        }
        if (agent != null) {
            MDC.put(HeaderKeys.AGENT_ID, agent);
        }

        RequestContext.set(parse(org), parse(user), parse(agent));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MDC.remove(HeaderKeys.ORG_ID);
        MDC.remove(HeaderKeys.USER_ID);
        MDC.remove(HeaderKeys.AGENT_ID);
        RequestContext.clear();
    }

    private UUID parse(String value) {
        try {
            return value == null ? null : UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
