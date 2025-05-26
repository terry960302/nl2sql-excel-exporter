package com.pandaterry.auth_microservice.infrastructure.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-token.expiration-time}")
    private long accessTokenExpirationTime;

    @Value("${jwt.refresh-token.expiration-time}")
    private long refreshTokenExpirationTime;

    public String getSecretKey() {
        return secretKey;
    }

    public long getAccessTokenExpirationTime() {
        return accessTokenExpirationTime;
    }

    public long getRefreshTokenExpirationTime() {
        return refreshTokenExpirationTime;
    }
}