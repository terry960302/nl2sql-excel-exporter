package com.pandaterry.gateway.presentation;

import com.pandaterry.gateway.infrastructure.client.AuthServiceClient;
import com.pandaterry.msa_contracts.constants.ApiPath;
import com.pandaterry.msa_contracts.dto.auth.request.LoginRequest;
import com.pandaterry.msa_contracts.dto.auth.request.SignupRequest;
import com.pandaterry.msa_contracts.dto.auth.response.TokenResponse;
import com.pandaterry.msa_contracts.dto.auth.response.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1" + ApiPath.Auth.BASE)
@RequiredArgsConstructor
public class AuthController {
    private final AuthServiceClient authServiceClient;

    @PostMapping(ApiPath.Auth.SIGNUP_SUFFIX)
    public ResponseEntity<Void> signup(@RequestBody SignupRequest request) {
        authServiceClient.signup(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiPath.Auth.LOGIN_SUFFIX)
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authServiceClient.login(request));
    }

    @GetMapping(ApiPath.Auth.ME_SUFFIX)
    public ResponseEntity<UserInfoResponse> me(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(authServiceClient.me(userId));
    }
}
