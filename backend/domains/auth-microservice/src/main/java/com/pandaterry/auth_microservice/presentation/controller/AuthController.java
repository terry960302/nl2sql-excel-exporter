package com.pandaterry.auth_microservice.presentation.controller;

import com.pandaterry.auth_microservice.application.service.AuthService;
import com.pandaterry.msa_contracts.constants.RoutePath;
import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.dto.auth.request.LoginRequest;
import com.pandaterry.msa_contracts.dto.auth.request.SignupRequest;
import com.pandaterry.msa_contracts.dto.auth.response.TokenResponse;
import com.pandaterry.msa_contracts.dto.auth.response.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1" + RoutePath.Auth.BASE)
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(RoutePath.Auth.SIGNUP_SUFFIX)
    public ResponseEntity<Void> signup(@RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping(RoutePath.Auth.LOGIN_SUFFIX)
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping(RoutePath.Auth.ME_SUFFIX)
    public ResponseEntity<UserInfoResponse> getMyInfo(@RequestHeader(HeaderKeys.USER_ID) String userId) {
        return ResponseEntity.ok(authService.getMyInfo(userId));
    }

    @PostMapping(RoutePath.Auth.REFRESH_TOKEN_SUFFIX)
    public ResponseEntity<TokenResponse> refreshToken(@RequestHeader(HeaderKeys.REFRESH_TOKEN) String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping(RoutePath.Auth.LOGOUT_SUFFIX)
    public ResponseEntity<Void> logout(@RequestHeader(HeaderKeys.REFRESH_TOKEN) String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.ok().build();
    }
}