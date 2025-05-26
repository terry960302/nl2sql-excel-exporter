package com.pandaterry.auth_microservice.infrastructure.util;

import com.pandaterry.auth_microservice.domain.entity.Organization;
import com.pandaterry.auth_microservice.domain.entity.Plan;
import com.pandaterry.auth_microservice.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private User testUser;
    private Organization testOrganization;
    private Plan testPlan;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(
                "testSecretKey123456789012345678901234567890",
                30L,
                7L);

        testPlan = new Plan();
        testPlan.setId(UUID.randomUUID());
        testPlan.setName("BASIC");
        testPlan.setMonthlyQuota(100);
        testPlan.setRateLimitRps(1);

        testOrganization = new Organization();
        testOrganization.setId(UUID.randomUUID());
        testOrganization.setName("default");
        testOrganization.setPlan(testPlan);

        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setOrganization(testOrganization);
    }

    @Test
    void generateAccessToken_성공() {
        // when
        String token = jwtUtil.generateAccessToken(
                testUser.getId(),
                testUser.getEmail(),
                testOrganization.getId(),
                testPlan.getName());

        // then
        assertThat(token).isNotNull();
        assertThat(jwtUtil.validateToken(token)).isTrue();
    }

    @Test
    void generateRefreshToken_성공() {
        // when
        String token = jwtUtil.generateRefreshToken(testUser.getId());

        // then
        assertThat(token).isNotNull();
        assertThat(jwtUtil.validateToken(token)).isTrue();
    }

    @Test
    void validateToken_유효한토큰_성공() {
        // given
        String token = jwtUtil.generateAccessToken(
                testUser.getId(),
                testUser.getEmail(),
                testOrganization.getId(),
                testPlan.getName());

        // when
        boolean isValid = jwtUtil.validateToken(token);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    void validateToken_유효하지않은토큰_실패() {
        // given
        String invalidToken = "invalid.token.here";

        // when
        boolean isValid = jwtUtil.validateToken(invalidToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    void getUserIdFromToken_성공() {
        // given
        String token = jwtUtil.generateAccessToken(
                testUser.getId(),
                testUser.getEmail(),
                testOrganization.getId(),
                testPlan.getName());

        // when
        UUID userId = jwtUtil.getUserIdFromToken(token);

        // then
        assertThat(userId).isEqualTo(testUser.getId());
    }
}