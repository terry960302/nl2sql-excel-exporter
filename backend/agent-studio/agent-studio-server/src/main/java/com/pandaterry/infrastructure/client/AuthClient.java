package com.pandaterry.infrastructure.client;

import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.dto.auth.request.LoginRequest;
import com.pandaterry.msa_contracts.dto.auth.request.SignupRequest;
import com.pandaterry.msa_contracts.dto.auth.response.TokenResponse;
import com.pandaterry.msa_contracts.dto.auth.response.UserInfoResponse;
import io.micronaut.http.annotation.Header;

public interface AuthClient {
    Void signup(SignupRequest request);
    TokenResponse login(LoginRequest request);
    UserInfoResponse me(@Header(HeaderKeys.AUTHORIZATION) String authorization);
    TokenResponse refreshToken(@Header(HeaderKeys.REFRESH_TOKEN) String refreshToken);

    Void logout(@Header(HeaderKeys.REFRESH_TOKEN) String refreshToken);
}
