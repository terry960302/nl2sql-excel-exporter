package com.pandaterry.schema_microservice.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;

@Entity
@Table(name = "aliases")
@Getter
@NoArgsConstructor
public class Alias {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(name = "column_id", nullable = false)
    private UUID columnId;

    @Column(name = "alias_name", nullable = false)
    private String aliasName;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_enabled", nullable = false)
    private EnableStatus isEnabled = EnableStatus.ENABLED;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static Alias create(UUID columnId, String aliasName, String description) {
        Alias alias = new Alias();
        alias.columnId = columnId;
        alias.aliasName = aliasName;
        alias.description = description;
        return alias;
    }

    public void updateNameAndDescription(String aliasName, String description) {
        Optional.ofNullable(aliasName).ifPresent((name) -> this.aliasName = name);
        Optional.ofNullable(description).ifPresent((desc) -> this.description = desc);
    }

    public void deactivate() {
        this.isEnabled = EnableStatus.DISABLED;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}