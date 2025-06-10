package com.pandaterry.auth_microservice.unit.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pandaterry.auth_microservice.application.service.AuthService;
import com.pandaterry.auth_microservice.domain.exception.AuthException;
import com.pandaterry.auth_microservice.domain.exception.ErrorCode;
import com.pandaterry.auth_microservice.config.SecurityTestConfig;
import com.pandaterry.auth_microservice.presentation.controller.AuthController;
import com.pandaterry.msa_contracts.constants.RoutePath;
import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.dto.auth.request.LoginRequest;
import com.pandaterry.msa_contracts.dto.auth.request.SignupRequest;
import com.pandaterry.msa_contracts.dto.auth.response.OrganizationResponse;
import com.pandaterry.msa_contracts.dto.auth.response.TokenResponse;
import com.pandaterry.msa_contracts.dto.auth.response.UserInfoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityTestConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    private final String VERSION = "/v1";

    @Nested
    @DisplayName("회원가입 관련 테스트")
    class SignupTest {
        @Test
        @WithMockUser
        @DisplayName("회원가입 성공")
        void signup_성공() throws Exception {
            // given
            SignupRequest request = SignupRequest.builder()
                    .name("asdkjaskdjlasd")
                    .email("test@example.com")
                    .password("ValidPass123!")
                    .build();

            // when & then
            mockMvc.perform(post(VERSION + RoutePath.Auth.SIGNUP)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        @DisplayName("이메일 중복으로 인한 회원가입 실패")
        void signup_이메일중복_실패() throws Exception {
            // given
            SignupRequest request = SignupRequest.builder()
                    .name("asdjhalksdjajsd")
                    .email("existing@example.com")
                    .password("ValidPass123!")
                    .build();

            Mockito.doThrow(new AuthException(ErrorCode.DUPLICATE_EMAIL)).when(authService).signup(any());

            // when & then
            mockMvc.perform(post(VERSION + RoutePath.Auth.SIGNUP)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value(ErrorCode.DUPLICATE_EMAIL.getCode()))
                    .andExpect(jsonPath("$.message").value(ErrorCode.DUPLICATE_EMAIL.getMessage()));
        }

        @Test
        @WithMockUser
        @DisplayName("비밀번호 정책 미준수로 인한 회원가입 실패")
        void signup_잘못된요청_실패() throws Exception {
            // given
            SignupRequest request = SignupRequest.builder()
                    .name("asdlkjalskdjasd")
                    .email("invalid-email")
                    .password("short")
                    .build();

            Mockito.doThrow(new AuthException(ErrorCode.WEAK_PASSWORD)).when(authService).signup(any());

            // when & then
            mockMvc.perform(post(VERSION + RoutePath.Auth.SIGNUP)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(ErrorCode.WEAK_PASSWORD.getCode()))
                    .andExpect(jsonPath("$.message").value(ErrorCode.WEAK_PASSWORD.getMessage()));
        }
    }

    @Nested
    @DisplayName("로그인 관련 테스트")
    class LoginTest {
        @Test
        @WithMockUser
        @DisplayName("로그인 성공")
        void login_성공() throws Exception {
            // given
            LoginRequest request = LoginRequest.builder()
                    .email("test@example.com")
                    .password("password123")
                    .build();

            TokenResponse tokenResponse = TokenResponse.builder()
                    .accessToken("accessToken")
                    .refreshToken("refreshToken")
                    .build();
            when(authService.login(any())).thenReturn(tokenResponse);

            // when & then
            mockMvc.perform(post(VERSION + RoutePath.Auth.LOGIN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("accessToken"))
                    .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
        }

        @Test
        @WithMockUser
        @DisplayName("잘못된 인증 정보로 인한 로그인 실패")
        void login_잘못된인증정보_실패() throws Exception {
            // given
            LoginRequest request = LoginRequest.builder()
                    .email("wrong@example.com")
                    .password("wrongpassword")
                    .build();

            when(authService.login(any())).thenThrow(new AuthException(ErrorCode.INVALID_CREDENTIALS));

            // when & then
            mockMvc.perform(post(VERSION + RoutePath.Auth.LOGIN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_CREDENTIALS.getCode()))
                    .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_CREDENTIALS.getMessage()));
        }
    }

    @Nested
    @DisplayName("사용자 정보 조회 테스트")
    class GetMyInfoTest {
        @Test
        @WithMockUser
        @DisplayName("사용자 정보 조회 성공")
        void getMyInfo_성공() throws Exception {
            // given
            String userId = UUID.randomUUID().toString();
            UserInfoResponse userInfo = UserInfoResponse.builder()
                    .userId(userId)
                    .email("test@example.com")
                    .organization(OrganizationResponse.builder()
                            .id("org-123")
                            .name("default")
                            .displayName("default")
                            .planName("BASIC")
                            .createdAt(LocalDateTime.now())
                            .build())
                    .planName("BASIC")
                    .totalQuota(100)
                    .usedQuota(30)
                    .remainingQuota(70)
                    .build();

            when(authService.getMyInfo(userId)).thenReturn(userInfo);

            // when & then
            mockMvc.perform(get(VERSION + RoutePath.Auth.ME)
                    .header(HeaderKeys.USER_ID, userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").value(userId))
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.organization.name").value("default"))
                    .andExpect(jsonPath("$.planName").value("BASIC"))
                    .andExpect(jsonPath("$.totalQuota").value(100))
                    .andExpect(jsonPath("$.usedQuota").value(30))
                    .andExpect(jsonPath("$.remainingQuota").value(70));
        }

        @Test
        @WithMockUser
        @DisplayName("존재하지 않는 사용자 정보 조회 실패")
        void getMyInfo_사용자없음_실패() throws Exception {
            // given
            String userId = UUID.randomUUID().toString();
            when(authService.getMyInfo(userId)).thenThrow(new AuthException(ErrorCode.USER_NOT_FOUND));

            // when & then
            mockMvc.perform(get(VERSION + RoutePath.Auth.ME)
                    .header(HeaderKeys.USER_ID, userId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.getCode()))
                    .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()));
        }
    }

    @Nested
    @DisplayName("토큰 관련 테스트")
    class TokenTest {
        @Test
        @WithMockUser
        @DisplayName("토큰 갱신 성공")
        void refreshToken_성공() throws Exception {
            // given
            String refreshToken = "validRefreshToken";
            TokenResponse tokenResponse = TokenResponse.builder()
                    .accessToken("newAccessToken")
                    .refreshToken("newRefreshToken")
                    .build();
            when(authService.refreshToken(refreshToken)).thenReturn(tokenResponse);

            // when & then
            mockMvc.perform(post(VERSION + RoutePath.Auth.REFRESH_TOKEN)
                    .header(HeaderKeys.REFRESH_TOKEN, refreshToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("newAccessToken"))
                    .andExpect(jsonPath("$.refreshToken").value("newRefreshToken"));
        }

        @Test
        @WithMockUser
        @DisplayName("유효하지 않은 토큰으로 인한 갱신 실패")
        void refreshToken_유효하지않은토큰_실패() throws Exception {
            // given
            String refreshToken = "invalidRefreshToken";
            when(authService.refreshToken(refreshToken)).thenThrow(new AuthException(ErrorCode.INVALID_TOKEN));

            // when & then
            mockMvc.perform(post(VERSION + RoutePath.Auth.REFRESH_TOKEN)
                    .header(HeaderKeys.REFRESH_TOKEN, refreshToken))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_TOKEN.getCode()))
                    .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_TOKEN.getMessage()));
        }

        @Test
        @WithMockUser
        @DisplayName("로그아웃 성공")
        void logout_성공() throws Exception {
            // given
            String refreshToken = "validRefreshToken";

            // when & then
            mockMvc.perform(post(VERSION + RoutePath.Auth.LOGOUT)
                    .header(HeaderKeys.REFRESH_TOKEN, refreshToken))
                    .andExpect(status().isOk());

            verify(authService).logout(refreshToken);
        }
    }
}