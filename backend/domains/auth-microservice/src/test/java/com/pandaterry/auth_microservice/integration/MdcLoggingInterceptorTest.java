package com.pandaterry.auth_microservice.integration;

import com.pandaterry.auth_microservice.presentation.config.WebConfig;
import com.pandaterry.auth_microservice.presentation.interceptor.MdcLoggingInterceptor;
import com.pandaterry.auth_microservice.integration.support.ContextEchoController;
import com.pandaterry.msa_contracts.constants.HeaderKeys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ContextEchoController.class)
@Import({MdcLoggingInterceptor.class, WebConfig.class})
class MdcLoggingInterceptorTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ApplicationContext applicationContext;

    @Test
    void interceptorRegistersAndSetsRequestContext() throws Exception {
        String org = UUID.randomUUID().toString();
        String user = UUID.randomUUID().toString();
        String agent = UUID.randomUUID().toString();

        mockMvc.perform(get("/test/context")
                        .header(HeaderKeys.ORG_ID, org)
                        .header(HeaderKeys.USER_ID, user)
                        .header(HeaderKeys.AGENT_ID, agent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orgId").value(org))
                .andExpect(jsonPath("$.userId").value(user))
                .andExpect(jsonPath("$.agentId").value(agent));

        assertThat(applicationContext.getBeansOfType(MdcLoggingInterceptor.class)).isNotEmpty();
    }
}
