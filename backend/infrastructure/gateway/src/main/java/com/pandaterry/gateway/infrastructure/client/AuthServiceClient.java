package com.pandaterry.gateway.infrastructure.client;

import com.pandaterry.msa_contracts.constants.ApiPath;
import com.pandaterry.msa_contracts.dto.auth.request.SignupRequest;
import com.pandaterry.msa_contracts.dto.auth.request.LoginRequest;
import com.pandaterry.msa_contracts.dto.auth.response.TokenResponse;
import com.pandaterry.msa_contracts.dto.auth.response.UserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service", url = "${auth-service.base-url}", path = "/api/v1")
public interface AuthServiceClient {
    @PostMapping(ApiPath.Auth.SIGNUP)
    Void signup(@RequestBody SignupRequest request);

    @PostMapping(ApiPath.Auth.LOGIN)
    TokenResponse login(@RequestBody LoginRequest request);

    @GetMapping(ApiPath.Auth.ME)
    UserInfoResponse me(@RequestHeader("X-User-Id") String userId);
}
