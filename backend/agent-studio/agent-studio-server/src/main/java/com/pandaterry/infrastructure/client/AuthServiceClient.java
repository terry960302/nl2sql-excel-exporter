package com.pandaterry.infrastructure.client;

import com.pandaterry.msa_contracts.constants.RoutePath;
import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.dto.auth.request.LoginRequest;
import com.pandaterry.msa_contracts.dto.auth.request.SignupRequest;
import com.pandaterry.msa_contracts.dto.auth.response.TokenResponse;
import com.pandaterry.msa_contracts.dto.auth.response.UserInfoResponse;
import io.micronaut.http.client.HttpClient;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class AuthServiceClient extends BaseServiceClient {

    private static final String PREFIX = "/api/v1";

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceClient.class);

    public AuthServiceClient(HttpClient httpClient, String baseUrl, String agentSecret) {
        super(httpClient, baseUrl, agentSecret);
    }

    // 회원가입
    public Void signup(SignupRequest request) {
        return post(PREFIX + RoutePath.Auth.SIGNUP, request, null, Void.class);
    }

    // 로그인
    public TokenResponse signin(LoginRequest request) {
        return post(PREFIX + RoutePath.Auth.LOGIN, request, null, TokenResponse.class);
    }

    // 내 정보 조회
    public UserInfoResponse me(UUID userId) {
        Map<String, String> header = new HashMap<>();
        header.put(HeaderKeys.USER_ID, userId.toString());
        return get(PREFIX + RoutePath.Auth.ME, header, UserInfoResponse.class);
    }

    // 리프레시 토큰 갱신
    public TokenResponse refreshToken(String refreshToken) {
        Map<String, String> header = new HashMap<>();
        header.put(HeaderKeys.REFRESH_TOKEN, refreshToken);
        return post(PREFIX + RoutePath.Auth.REFRESH_TOKEN, header, TokenResponse.class);
    }

    // 로그아웃
    public Void logout(String refreshToken){
        Map<String, String> header = new HashMap<>();
        header.put(HeaderKeys.REFRESH_TOKEN, refreshToken);
        return post(PREFIX + RoutePath.Auth.LOGOUT, header, Void.class);
    }
}