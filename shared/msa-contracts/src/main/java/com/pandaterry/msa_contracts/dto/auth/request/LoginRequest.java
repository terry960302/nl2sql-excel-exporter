package com.pandaterry.msa_contracts.dto.auth.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class LoginRequest {
    @NonNull
    private String email;
    @NonNull
    private String password;
}