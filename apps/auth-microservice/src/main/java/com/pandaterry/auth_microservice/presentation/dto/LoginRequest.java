package com.pandaterry.auth_microservice.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    private String email;
    private String password;

    public static LoginRequest of(String email, String password) {
        return LoginRequest.builder()
                .email(email)
                .password(password)
                .build();
    }
}