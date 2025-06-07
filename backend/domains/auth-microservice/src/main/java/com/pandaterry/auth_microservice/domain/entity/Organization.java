package com.pandaterry.auth_microservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import com.pandaterry.auth_microservice.domain.exception.AuthException;
import com.pandaterry.auth_microservice.domain.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "organizations" )
@Getter
@Setter
@NoArgsConstructor
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;  // org-UUID 형식의 고유 이름

    @Column(name = "display_name", nullable = false)
    private String displayName;  // 사용자가 볼 수 있는 표시 이름

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 생성 메서드
    public static Organization createDefault(Plan plan) {
        Organization org = new Organization();
        org.name = "org-" + UUID.randomUUID();
        org.displayName = "Default Organization";
        org.plan = plan;
        return org;
    }

    // 표시 이름 변경 메서드
    public void updateDisplayName(String newDisplayName) {
        if (newDisplayName == null || newDisplayName.trim().isEmpty()) {
            throw new AuthException(ErrorCode.DISPLAY_NAME_EMPTY);
        }
        this.displayName = newDisplayName;
    }
}