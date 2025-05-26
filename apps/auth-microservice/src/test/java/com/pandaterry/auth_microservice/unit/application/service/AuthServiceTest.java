package com.pandaterry.auth_microservice.unit.application.service;

import com.pandaterry.auth_microservice.application.service.AuthService;
import com.pandaterry.auth_microservice.infrastructure.client.QuotaClient;
import com.pandaterry.auth_microservice.domain.entity.Organization;
import com.pandaterry.auth_microservice.domain.entity.Plan;
import com.pandaterry.auth_microservice.domain.entity.RefreshToken;
import com.pandaterry.auth_microservice.domain.entity.User;
import com.pandaterry.auth_microservice.domain.exception.AuthException;
import com.pandaterry.auth_microservice.domain.repository.OrganizationRepository;
import com.pandaterry.auth_microservice.domain.repository.PlanRepository;
import com.pandaterry.auth_microservice.domain.repository.RefreshTokenRepository;
import com.pandaterry.auth_microservice.domain.repository.UserRepository;
import com.pandaterry.auth_microservice.infrastructure.util.JwtUtil;
import com.pandaterry.auth_microservice.presentation.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private PlanRepository planRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private QuotaClient quotaClient;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private Organization testOrganization;
    private Plan testPlan;
    private RefreshToken testRefreshToken;

    @BeforeEach
    void setUp() {
        testPlan = new Plan();
        testPlan.setId(UUID.randomUUID());
        testPlan.setName("BASIC");
        testPlan.setMonthlyQuota(100);
        testPlan.setRateLimitRps(1);

        testOrganization = new Organization();
        testOrganization.setId(UUID.randomUUID());
        testOrganization.setName("default");
        testOrganization.setPlan(testPlan);

        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("encodedPassword");
        testUser.setOrganization(testOrganization);

        testRefreshToken = RefreshToken.create(testUser, "testRefreshToken", LocalDateTime.now(), LocalDateTime.now().plusDays(30));
    }

    @Test
    void signup_성공() {
        // given
        SignupRequest request = SignupRequest.builder()
                .email("new@example.com")
                .password("ValidPass123!")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(planRepository.findByName("BASIC")).thenReturn(Optional.of(testPlan));
        when(organizationRepository.save(any(Organization.class))).thenReturn(testOrganization);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        authService.signup(request);

        // then
        verify(userRepository).existsByEmail(request.getEmail());
        verify(planRepository).findByName("BASIC");
        verify(organizationRepository).save(any(Organization.class));
        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signup_이메일중복_실패() {
        // given
        SignupRequest request = SignupRequest.builder()
                .email("existing@example.com")
                .password("ValidPass123!")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(AuthException.class);
    }

    @Test
    void login_성공() {
        // given
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(request.getPassword(), testUser.getPasswordHash())).thenReturn(true);
        when(jwtUtil.generateAccessToken(any(), any(), any(), any())).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(any())).thenReturn("refreshToken");

        // when
        TokenResponse response = authService.login(request);

        // then
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void login_잘못된이메일_실패() {
        // given
        LoginRequest request = LoginRequest.builder()
                .email("wrong@example.com")
                .password("password123")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthException.class);
    }

    @Test
    void getMyInfo_성공() {
        // given
        String userId = testUser.getId().toString();
        QuotaInfo quotaInfo = new QuotaInfo(100, 30, 70);

        when(userRepository.findById(UUID.fromString(userId))).thenReturn(Optional.of(testUser));
        when(quotaClient.getCurrentQuota(userId)).thenReturn(quotaInfo);

        // when
        UserInfoResponse response = authService.getMyInfo(userId);

        // then
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(response.getOrganization().getId()).isEqualTo(testOrganization.getId().toString());
        assertThat(response.getOrganization().getName()).isEqualTo(testOrganization.getName());
        assertThat(response.getOrganization().getDisplayName()).isEqualTo(testOrganization.getDisplayName());
        assertThat(response.getOrganization().getPlanName()).isEqualTo(testPlan.getName());
        assertThat(response.getOrganization().getCreatedAt()).isEqualTo(testOrganization.getCreatedAt());
        assertThat(response.getPlanName()).isEqualTo(testPlan.getName());
        assertThat(response.getTotalQuota()).isEqualTo(quotaInfo.getTotalQuota());
        assertThat(response.getUsedQuota()).isEqualTo(quotaInfo.getUsedQuota());
        assertThat(response.getRemainingQuota()).isEqualTo(quotaInfo.getRemainingQuota());
    }

    @Test
    void refreshToken_성공() {
        // given
        String refreshToken = "validRefreshToken";
        when(jwtUtil.validateToken(refreshToken)).thenReturn(true);
        when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.of(testRefreshToken));
        when(jwtUtil.generateAccessToken(any(), any(), any(), any())).thenReturn("newAccessToken");
        when(jwtUtil.generateRefreshToken(any())).thenReturn("newRefreshToken");

        // when
        TokenResponse response = authService.refreshToken(refreshToken);

        // then
        assertThat(response.getAccessToken()).isEqualTo("newAccessToken");
        assertThat(response.getRefreshToken()).isEqualTo("newRefreshToken");
        assertThat(testRefreshToken.isRevoked()).isTrue();
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void logout_성공() {
        // given
        String refreshToken = "validRefreshToken";
        when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.of(testRefreshToken));

        // when
        authService.logout(refreshToken);

        // then
        assertThat(testRefreshToken.isRevoked()).isTrue();
        verify(refreshTokenRepository).save(testRefreshToken);
    }
}