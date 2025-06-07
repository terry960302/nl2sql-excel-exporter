package com.pandaterry.auth_microservice.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean revoked;


    public void revoke() {
        this.revoked = true;
    }

    public boolean isExpired(){
       return this.expiresAt.isBefore(LocalDateTime.now());
    }

    private RefreshToken(User user, String token, LocalDateTime issuedAt, LocalDateTime expiresAt, boolean revoked) {
        this.user = user;
        this.token = token;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
    }

    public static RefreshToken create(User user, String token, LocalDateTime issuedAt, LocalDateTime expiresAt) {
        return new RefreshToken(user, token, issuedAt, expiresAt, false);
    }
}