package com.pandaterry.gateway.shared.config;

import com.pandaterry.msa_contracts.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    @Value("${jwt.secret-key}")
    String secretKey;
    @Value("${jwt.access-token.expiration-time}")
    long accessTokenExpirationTime;
    @Value("${jwt.refresh-token.expiration-time}")
    long refreshTokenExpirationTime;

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(secretKey, accessTokenExpirationTime, refreshTokenExpirationTime);
    }
}