package com.pandaterry.auth_microservice.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {
    private String email;
    private String password;

    public static SignupRequest of(String email, String password) {
        return SignupRequest.builder()
                .email(email)
                .password(password)
                .build();
    }
}