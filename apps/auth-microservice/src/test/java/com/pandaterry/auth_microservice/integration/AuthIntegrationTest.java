// src/test/java/com/pandaterry/auth_microservice/integration/AuthIntegrationTest.java
package com.pandaterry.auth_microservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pandaterry.auth_microservice.domain.entity.Organization;
import com.pandaterry.auth_microservice.domain.entity.Plan;
import com.pandaterry.auth_microservice.domain.entity.User;
import com.pandaterry.auth_microservice.domain.enumerated.PlanType;
import com.pandaterry.auth_microservice.domain.exception.AuthException;
import com.pandaterry.auth_microservice.domain.exception.ErrorCode;
import com.pandaterry.auth_microservice.domain.repository.OrganizationRepository;
import com.pandaterry.auth_microservice.domain.repository.PlanRepository;
import com.pandaterry.auth_microservice.domain.repository.UserRepository;
import com.pandaterry.auth_microservice.infrastructure.config.SecurityTestConfig;
import com.pandaterry.auth_microservice.presentation.dto.LoginRequest;
import com.pandaterry.auth_microservice.presentation.dto.SignupRequest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityTestConfig.class)
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private UserRepository userRepository;

    // 기본으로업로드 되어있어야하는 데이터
    private Plan basicPlan;

    @BeforeEach
    void setUp() {
        // 기본 플랜 생성
        basicPlan = new Plan();
        basicPlan.setName(PlanType.BASIC.name());
        basicPlan.setMonthlyQuota(100);
        basicPlan.setRateLimitRps(1);
        planRepository.save(basicPlan);
    }

    @Nested
    @DisplayName("회원가입 통합 테스트")
    class SignupIntegrationTest {
        @Test
        @DisplayName("회원가입 성공")
        void signup_성공() throws Exception {
            // given
            SignupRequest request = SignupRequest.builder()
                    .email("test@example.com")
                    .password("ValidPass123!")
                    .build();

            // when & then
            mockMvc.perform(post("/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            // then
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

            Assertions.assertEquals(user.getEmail(), request.getEmail());
            Assertions.assertEquals(user.getPasswordHash(), request.getPassword());

        }

        @Test
        @DisplayName("이메일 중복으로 인한 회원가입 실패")
        void signup_이메일중복_실패() throws Exception {
            // given
            SignupRequest request = SignupRequest.builder()
                    .email("test@example.com")
                    .password("ValidPass123!")
                    .build();

            // 첫 번째 회원가입
            mockMvc.perform(post("/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            // 두 번째 회원가입 시도
            mockMvc.perform(post("/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value(ErrorCode.DUPLICATE_EMAIL.getCode()));
        }
    }

    @Nested
    @DisplayName("로그인 통합 테스트")
    class LoginIntegrationTest {
        private String userEmail;
        private String userPassword;

        @BeforeEach
        void setUp() {
            userEmail = "test@example.com";
            userPassword = "ValidPass123!";

            // 회원가입
            SignupRequest signupRequest = SignupRequest.builder()
                    .email(userEmail)
                    .password(userPassword)
                    .build();

            try {
                mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                        .andExpect(status().isOk());
            } catch (Exception e) {
                throw new RuntimeException("회원가입 실패", e);
            }
        }

        @Test
        @DisplayName("로그인 성공")
        void login_성공() throws Exception {
            // given
            LoginRequest request = LoginRequest.builder()
                    .email(userEmail)
                    .password(userPassword)
                    .build();

            // when & then
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").exists())
                    .andExpect(jsonPath("$.refreshToken").exists());
        }

        @Test
        @DisplayName("잘못된 비밀번호로 인한 로그인 실패")
        void login_잘못된비밀번호_실패() throws Exception {
            // given
            LoginRequest request = LoginRequest.builder()
                    .email(userEmail)
                    .password("WrongPassword123!")
                    .build();

            // when & then
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("AUTH_4002"));
        }
    }
}