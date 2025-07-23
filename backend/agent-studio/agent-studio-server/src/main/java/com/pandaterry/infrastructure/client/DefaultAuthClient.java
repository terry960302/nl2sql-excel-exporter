package com.pandaterry.infrastructure.client;

import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.constants.RoutePath;
import com.pandaterry.msa_contracts.dto.auth.request.LoginRequest;
import com.pandaterry.msa_contracts.dto.auth.request.SignupRequest;
import com.pandaterry.msa_contracts.dto.auth.response.TokenResponse;
import com.pandaterry.msa_contracts.dto.auth.response.UserInfoResponse;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client(id = "gateway", path = RoutePath.Auth.BASE)
public interface DefaultAuthClient extends AuthClient {
    @Post(RoutePath.Auth.SIGNUP_SUFFIX)
    @Override
    Void signup(SignupRequest request);

    @Post(RoutePath.Auth.LOGIN_SUFFIX)
    @Override
    TokenResponse login(LoginRequest request);

    @Get(RoutePath.Auth.ME_SUFFIX)
    @Override
    UserInfoResponse me(@Header(HeaderKeys.AUTHORIZATION) String authorization);

    @Post(RoutePath.Auth.REFRESH_TOKEN_SUFFIX)
    @Override
    TokenResponse refreshToken(@Header(HeaderKeys.REFRESH_TOKEN) String refreshToken);

    @Post(RoutePath.Auth.LOGOUT_SUFFIX)
    @Override
    Void logout(@Header(HeaderKeys.REFRESH_TOKEN) String refreshToken);


}
