package com.pandaterry.auth_microservice.application.service;

import com.pandaterry.auth_microservice.infrastructure.client.QuotaClient;
import com.pandaterry.auth_microservice.domain.entity.Organization;
import com.pandaterry.auth_microservice.domain.entity.Plan;
import com.pandaterry.auth_microservice.domain.entity.RefreshToken;
import com.pandaterry.auth_microservice.domain.entity.User;
import com.pandaterry.auth_microservice.domain.enumerated.PlanType;
import com.pandaterry.auth_microservice.domain.exception.AuthException;
import com.pandaterry.auth_microservice.domain.exception.ErrorCode;
import com.pandaterry.auth_microservice.domain.repository.OrganizationRepository;
import com.pandaterry.auth_microservice.domain.repository.PlanRepository;
import com.pandaterry.auth_microservice.domain.repository.RefreshTokenRepository;
import com.pandaterry.auth_microservice.domain.repository.UserRepository;
import com.pandaterry.auth_microservice.application.validator.PasswordValidator;
import com.pandaterry.auth_microservice.infrastructure.util.JwtUtil;
import com.pandaterry.auth_microservice.presentation.mappers.UserInfoMapper;
import com.pandaterry.msa_contracts.dto.auth.request.LoginRequest;
import com.pandaterry.msa_contracts.dto.auth.request.SignupRequest;
import com.pandaterry.msa_contracts.dto.auth.response.QuotaInfo;
import com.pandaterry.msa_contracts.dto.auth.response.TokenResponse;
import com.pandaterry.msa_contracts.dto.auth.response.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PlanRepository planRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final QuotaClient quotaClient;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public void signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException(ErrorCode.DUPLICATE_EMAIL);
        }
        PasswordValidator.validate(request.getPassword());

        Plan basicPlan = planRepository.findByName(PlanType.BASIC.name())
                .orElseThrow(() -> new AuthException(ErrorCode.PLAN_NOT_FOUND));

        Organization organization = Organization.createDefault(basicPlan);

        organization = organizationRepository.save(organization);

        User user = User.create(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                organization
        );

        userRepository.save(user);
    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AuthException(ErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtUtil.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getOrganization().getId(),
                user.getOrganization().getPlan().getName());

        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        saveRefreshToken(user, refreshToken);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getMyInfo(String userId) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

        QuotaInfo quotaInfo = quotaClient.getCurrentQuota(userId);

        return UserInfoMapper.toResponse(user, quotaInfo);
    }

    public TokenResponse refreshToken(String refreshToken) {
        jwtUtil.validateToken(refreshToken);

        RefreshToken existingToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new AuthException(ErrorCode.INVALID_TOKEN));

        if (existingToken.isRevoked()) {
            throw new AuthException(ErrorCode.TOKEN_REVOKED);
        }

        // 기존 토큰은 폐기처리(더이상 사용못하도록)
        existingToken.revoke();

        User user = existingToken.getUser();

        String newAccessToken = jwtUtil.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getOrganization().getId(),
                user.getOrganization().getPlan().getName());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getId());

        saveRefreshToken(user, newRefreshToken);

        return TokenResponse.of(newAccessToken, newRefreshToken);
    }

    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(token -> {
                    token.revoke();
                    refreshTokenRepository.save(token);
                });
    }

    private void saveRefreshToken(User user, String token) {
        RefreshToken refreshToken = RefreshToken.create(user, token, LocalDateTime.now(), LocalDateTime.now().plusDays(30));
        refreshTokenRepository.save(refreshToken);
    }
}