package com.pandaterry.auth_microservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pandaterry.auth_microservice.AuthMicroserviceApplication;
import com.pandaterry.auth_microservice.domain.entity.RefreshToken;
import com.pandaterry.auth_microservice.domain.entity.User;
import com.pandaterry.auth_microservice.domain.exception.AuthException;
import com.pandaterry.auth_microservice.domain.exception.ErrorCode;
import com.pandaterry.auth_microservice.domain.repository.PlanRepository;
import com.pandaterry.auth_microservice.domain.repository.RefreshTokenRepository;
import com.pandaterry.auth_microservice.domain.repository.UserRepository;
import com.pandaterry.auth_microservice.config.SecurityTestConfig;
import com.pandaterry.auth_microservice.infrastructure.client.QuotaClient;
import com.pandaterry.auth_microservice.infrastructure.util.JwtUtil;
import com.pandaterry.msa_contracts.constants.ApiPath;
import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.dto.auth.request.LoginRequest;
import com.pandaterry.msa_contracts.dto.auth.request.SignupRequest;
import com.pandaterry.msa_contracts.dto.auth.response.QuotaInfo;
import com.pandaterry.msa_contracts.dto.auth.response.TokenResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 모든 빈을 스캔하지 않도록(메모리 효율화) 특정 클래스만 지정해서 테스트 시행
@SpringBootTest(
        classes = {
                AuthMicroserviceApplication.class,
                SecurityTestConfig.class
        }
)
@AutoConfigureMockMvc
@Import(SecurityTestConfig.class)
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuotaClient quotaClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private static final String VERSION = "/v1";

    @BeforeEach
    void setUp() {
        // QuotaClient의 임의 설정
        when(quotaClient.getCurrentQuota(anyString()))
                .thenReturn(QuotaInfo.of(100, 0, 100));  // 기본 쿼터 정보 반환
    }

    @Nested
    @DisplayName("회원가입 통합 테스트")
    class SignupIntegrationTest {
        @Test
        @DisplayName("회원가입 성공")
        void signup_성공() throws Exception {
            // given
            SignupRequest request = SignupRequest.builder()
                    .name("hello")
                    .email("test@example.com")
                    .password("ValidPass123!")
                    .build();

            // when
            mockMvc.perform(post(VERSION + ApiPath.Auth.SIGNUP)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            // then
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

            Assertions.assertEquals(request.getEmail(), user.getEmail());
        }

        @Test
        @DisplayName("이메일 중복으로 인한 회원가입 실패")
        void signup_이메일중복_실패() throws Exception {
            // given
            SignupRequest request = SignupRequest.builder()
                    .name("dkssud")
                    .email("test1@example.com")
                    .password("ValidPass123!")
                    .build();

            // 첫 번째 회원가입
            mockMvc.perform(post(VERSION + ApiPath.Auth.SIGNUP)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            // 두 번째 회원가입 시도
            mockMvc.perform(post(VERSION + ApiPath.Auth.SIGNUP)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value(ErrorCode.DUPLICATE_EMAIL.getCode()))
                    .andExpect(jsonPath("$.message").value(ErrorCode.DUPLICATE_EMAIL.getMessage()));
        }
    }

    @Nested
    @DisplayName("로그인 통합 테스트")
    class LoginIntegrationTest {
        private String username;
        private String userEmail;
        private String userPassword;

        @BeforeEach
        void setUp() {
            username = "ㅁㄴ어ㅣㅏㅁ넝";
            userEmail = "test@example.com";
            userPassword = "ValidPass123!";

            // 회원가입
            SignupRequest signupRequest = SignupRequest.builder()
                    .name(username)
                    .email(userEmail)
                    .password(userPassword)
                    .build();

            try {
                mockMvc.perform(post(VERSION + ApiPath.Auth.SIGNUP)
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
            mockMvc.perform(post(VERSION + ApiPath.Auth.LOGIN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").exists())
                    .andExpect(jsonPath("$.refreshToken").exists())
                    .andExpect(jsonPath("$.accessToken").isString())
                    .andExpect(jsonPath("$.refreshToken").isString());
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
            mockMvc.perform(post(VERSION + ApiPath.Auth.LOGIN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_CREDENTIALS.getCode()));
        }
    }

    @Nested
    @DisplayName("토큰 갱신 통합 테스트")
    class TokenRefreshIntegrationTest {
        private User testUser;

        private String username = "안녕";
        private String userEmail = "test@example.com";
        private String userPassword = "ValidPass123!";

        private TokenResponse tokenResponse;

        @BeforeEach
        void setUp() throws Exception {
            // 회원가입 요청으로 사용자 생성
            SignupRequest signupRequest = SignupRequest.builder()
                    .name(username)
                    .email(userEmail)
                    .password(userPassword)
                    .build();

            mockMvc.perform(post(VERSION + ApiPath.Auth.SIGNUP)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isOk());

            // 사용자 정보 조회
            testUser = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

            // 로그인하여 토큰 발급
            LoginRequest loginRequest = LoginRequest.builder()
                    .email(userEmail)
                    .password(userPassword)
                    .build();

            String loginResponse = mockMvc.perform(post(VERSION + ApiPath.Auth.LOGIN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").exists())
                    .andExpect(jsonPath("$.refreshToken").exists())
                    .andExpect(jsonPath("$.accessToken").isString())
                    .andExpect(jsonPath("$.refreshToken").isString())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            tokenResponse = objectMapper.readValue(loginResponse, TokenResponse.class);
        }

        @Test
        @DisplayName("토큰 갱신 성공")
        void refreshToken_성공() throws Exception {

            String validRefreshToken = tokenResponse.getRefreshToken();
            // when & then
            mockMvc.perform(post(VERSION + ApiPath.Auth.REFRESH_TOKEN)
                            .header(HeaderKeys.REFRESH_TOKEN, validRefreshToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").exists())
                    .andExpect(jsonPath("$.refreshToken").exists())
                    .andExpect(jsonPath("$.accessToken").isString())
                    .andExpect(jsonPath("$.refreshToken").isString())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // 새로운 토큰으로 사용자 정보 조회 테스트
            mockMvc.perform(get(VERSION + ApiPath.Auth.ME)
                            .header(HeaderKeys.USER_ID, testUser.getId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(userEmail))
                    .andExpect(jsonPath("$.userId").value(testUser.getId().toString()));
        }

        @Test
        @DisplayName("만료된 리프레시 토큰으로 갱신 실패")
        void refreshToken_만료된토큰_실패() throws Exception {
            RefreshToken token = refreshTokenRepository.findByToken(tokenResponse.getRefreshToken())
                    .orElseThrow(() -> new AuthException(ErrorCode.INVALID_TOKEN));
            refreshTokenRepository.delete(token);

            LocalDateTime expiredAt = LocalDateTime.now().minus(1, ChronoUnit.DAYS);
            String expiredRefreshToken = jwtUtil.generateRefreshToken(testUser.getId(), Date.from(expiredAt.atZone(ZoneId.systemDefault()) // or ZoneOffset.UTC
                    .toInstant()));
            RefreshToken newToken = RefreshToken.create(testUser, expiredRefreshToken, LocalDateTime.now(), expiredAt);
            refreshTokenRepository.save(newToken);

            // when & then
            mockMvc.perform(post(VERSION + ApiPath.Auth.REFRESH_TOKEN)
                            .header(HeaderKeys.REFRESH_TOKEN, expiredRefreshToken))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(ErrorCode.TOKEN_EXPIRED.getCode()))
                    .andExpect(jsonPath("$.message").value(ErrorCode.TOKEN_EXPIRED.getMessage()));
        }

        @Test
        @DisplayName("이미 사용된 리프레시 토큰으로 갱신 실패")
        void refreshToken_이미사용된토큰_실패() throws Exception {
            RefreshToken token = refreshTokenRepository.findByToken(tokenResponse.getRefreshToken())
                    .orElseThrow(() -> new AuthException(ErrorCode.INVALID_TOKEN));
            token.revoke();
            refreshTokenRepository.save(token);


            // when & then
            mockMvc.perform(post(VERSION + ApiPath.Auth.REFRESH_TOKEN)
                            .header(HeaderKeys.REFRESH_TOKEN, token.getToken()))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(ErrorCode.TOKEN_REVOKED.getCode()))
                    .andExpect(jsonPath("$.message").value(ErrorCode.TOKEN_REVOKED.getMessage()));
        }

        @Test
        @DisplayName("유효하지 않은 리프레시 토큰으로 갱신 실패")
        void refreshToken_유효하지않은토큰_실패() throws Exception {
            // when & then
            mockMvc.perform(post(VERSION + ApiPath.Auth.REFRESH_TOKEN)
                            .header(HeaderKeys.REFRESH_TOKEN, "invalid.token.here"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_TOKEN.getCode()))
                    .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_TOKEN.getMessage()));
        }

        @Test
        @DisplayName("토큰 갱신 후 이전 토큰으로 재사용 시도 실패")
        void refreshToken_이전토큰재사용_실패() throws Exception {
            String firstRefreshToken = tokenResponse.getRefreshToken();
            // 첫 번째 토큰 갱신
            String response = mockMvc.perform(post(VERSION + ApiPath.Auth.REFRESH_TOKEN)
                            .header(HeaderKeys.REFRESH_TOKEN, firstRefreshToken))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // 새로 갱신받은 토큰
            TokenResponse secondTokens = objectMapper.readValue(response, TokenResponse.class);
            String secondRefreshToken = secondTokens.getRefreshToken();

            // 이전 토큰으로 재시도
            mockMvc.perform(post(VERSION + ApiPath.Auth.REFRESH_TOKEN)
                            .header(HeaderKeys.REFRESH_TOKEN, firstRefreshToken)) // 첫번째 발급받은 토큰은 이미 폐기처리돼서 갱신을 못함.
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(ErrorCode.TOKEN_REVOKED.getCode()))
                    .andExpect(jsonPath("$.message").value(ErrorCode.TOKEN_REVOKED.getMessage()));

            // 두번째로 받은(갱신한) 새로운 토큰으로 시도 -> 성공해야함.
            mockMvc.perform(post(VERSION + ApiPath.Auth.REFRESH_TOKEN)
                            .header(HeaderKeys.REFRESH_TOKEN, secondRefreshToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").exists())
                    .andExpect(jsonPath("$.refreshToken").exists());
        }
    }
}