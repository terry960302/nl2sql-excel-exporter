package com.pandaterry.infrastructure.client;

import com.pandaterry.domain.model.ExecutionJob;
import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.dto.auth.request.LoginRequest;
import com.pandaterry.msa_contracts.dto.auth.request.SignupRequest;
import com.pandaterry.msa_contracts.dto.auth.response.TokenResponse;
import com.pandaterry.msa_contracts.dto.auth.response.UserInfoResponse;
import com.pandaterry.msa_contracts.dto.query.request.JobResultRequest;
import com.pandaterry.presentation.dto.request.QueryRequest;
import io.micronaut.http.client.HttpClient;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class AuthServiceClient extends BaseServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceClient.class);

    public AuthServiceClient(HttpClient httpClient, String baseUrl, String agentSecret) {
        super(httpClient, baseUrl, agentSecret);
    }

    // 회원가입
    public Void signup(SignupRequest request) {
        return post("/api/v1/auth/signup", request, null, Void.class);
    }

    // 로그인
    public TokenResponse signin(LoginRequest request) {
        return post("/api/v1/auth/login", request, null, TokenResponse.class);
    }

    // 내 정보 조회
    public UserInfoResponse me(UUID userId) {
        Map<String, String> header = new HashMap<>();
        header.put(HeaderKeys.USER_ID, userId.toString());
        return get("/api/v1/auth/me", header, UserInfoResponse.class);
    }

    // 리프레시 토큰 갱신
    public TokenResponse refreshToken(String refreshToken) {
        Map<String, String> header = new HashMap<>();
        header.put(HeaderKeys.REFRESH_TOKEN, refreshToken);
        return post("/api/v1/token/refresh", header, TokenResponse.class);
    }

    // 로그아웃
    public Void logout(String refreshToken){
        Map<String, String> header = new HashMap<>();
        header.put(HeaderKeys.REFRESH_TOKEN, refreshToken);
        return post("/api/v1/logout", header, Void.class);
    }
}