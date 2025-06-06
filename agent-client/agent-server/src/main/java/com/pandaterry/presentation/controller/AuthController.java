package com.pandaterry.presentation.controller;

import com.pandaterry.infrastructure.client.AuthServiceClient;
import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.dto.auth.request.LoginRequest;
import com.pandaterry.msa_contracts.dto.auth.request.SignupRequest;
import com.pandaterry.msa_contracts.dto.auth.response.TokenResponse;
import com.pandaterry.msa_contracts.dto.auth.response.UserInfoResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;

import java.util.UUID;

@Controller("/api/v1/auth")
public class AuthController {

    @Inject
    private AuthServiceClient authServiceClient;

    @Post("/signup")
    public HttpResponse<Void> signup(@Body SignupRequest request) {
        return HttpResponse.ok(authServiceClient.signup(request));
    }

    @Post("/login")
    public HttpResponse<TokenResponse> login(@Body LoginRequest request) {
        return HttpResponse.ok(authServiceClient.signin(request));
    }

    @Get("/me")
    public HttpResponse<UserInfoResponse> getMe(@Header(HeaderKeys.USER_ID) UUID userId) {
        return HttpResponse.ok(authServiceClient.me(userId));
    }

    @Post("/token/refresh")
    public HttpResponse<TokenResponse> refreshToken(@Header(HeaderKeys.REFRESH_TOKEN) String refreshToken){
        return HttpResponse.ok(authServiceClient.refreshToken(refreshToken));
    }

    @Post("logout")
    public HttpResponse<Void> logout(@Header(HeaderKeys.REFRESH_TOKEN) String refreshToken){
        return HttpResponse.ok(authServiceClient.logout(refreshToken));
    }
}
