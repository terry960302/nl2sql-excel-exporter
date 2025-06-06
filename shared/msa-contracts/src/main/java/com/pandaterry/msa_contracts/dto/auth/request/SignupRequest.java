package com.pandaterry.msa_contracts.dto.auth.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {
    @NonNull
    private String name;
    @NonNull
    private String email;
    @NonNull
    private String password;

}