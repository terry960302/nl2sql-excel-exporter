package com.pandaterry.auth_microservice.presentation.controller;

import com.pandaterry.auth_microservice.application.service.AuthService;
import com.pandaterry.msa_contracts.constants.ApiPath;
import com.pandaterry.msa_contracts.dto.auth.request.LoginRequest;
import com.pandaterry.msa_contracts.dto.auth.request.SignupRequest;
import com.pandaterry.msa_contracts.dto.auth.response.TokenResponse;
import com.pandaterry.msa_contracts.dto.auth.response.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1" + ApiPath.Auth.BASE)
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(ApiPath.Auth.SIGNUP_SUFFIX)
    public ResponseEntity<Void> signup(@RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiPath.Auth.LOGIN_SUFFIX)
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping(ApiPath.Auth.ME_SUFFIX)
    public ResponseEntity<UserInfoResponse> getMyInfo(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(authService.getMyInfo(userId));
    }

    @PostMapping(ApiPath.Auth.REFRESH_TOKEN_SUFFIX)
    public ResponseEntity<TokenResponse> refreshToken(@RequestHeader("X-Refresh-Token") String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping(ApiPath.Auth.LOGOUT_SUFFIX)
    public ResponseEntity<Void> logout(@RequestHeader("X-Refresh-Token") String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.ok().build();
    }
}