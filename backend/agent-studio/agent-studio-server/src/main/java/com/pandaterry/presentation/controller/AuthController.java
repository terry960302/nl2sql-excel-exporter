package com.pandaterry.presentation.controller;

import com.pandaterry.infrastructure.client.DefaultAuthClient;
import com.pandaterry.msa_contracts.constants.RoutePath;
import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.dto.auth.request.LoginRequest;
import com.pandaterry.msa_contracts.dto.auth.request.SignupRequest;
import com.pandaterry.msa_contracts.dto.auth.response.TokenResponse;
import com.pandaterry.msa_contracts.dto.auth.response.UserInfoResponse;
import com.pandaterry.shared.utils.HeaderUtil;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;

@Controller(RoutePath.Auth.BASE)
public class AuthController {

    @Inject
    private DefaultAuthClient authClient;

    @Post(RoutePath.Auth.SIGNUP_SUFFIX)
    public HttpResponse<Void> signup(@Body SignupRequest request) {
        return HttpResponse.ok(authClient.signup(request));
    }

    @Post(RoutePath.Auth.LOGIN_SUFFIX)
    public HttpResponse<TokenResponse> login(@Body LoginRequest request) {
        return HttpResponse.ok(authClient.login(request));
    }

    @Get(RoutePath.Auth.ME_SUFFIX)
    public HttpResponse<UserInfoResponse> me(@Header(HeaderKeys.AUTHORIZATION) String authorization) {
        return HttpResponse.ok(authClient.me(authorization));
    }

    @Post(RoutePath.Auth.REFRESH_TOKEN_SUFFIX)
    public HttpResponse<TokenResponse> refreshToken(@Header(HeaderKeys.REFRESH_TOKEN) String refreshToken) {
        return HttpResponse.ok(authClient.refreshToken(refreshToken));
    }

    @Post(RoutePath.Auth.LOGOUT_SUFFIX)
    public HttpResponse<Void> logout(@Header(HeaderKeys.REFRESH_TOKEN) String refreshToken) {
        return HttpResponse.ok(authClient.logout(refreshToken));
    }
}
