package com.pandaterry.schema_microservice.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;

@Entity
@Table(name = "schemas")
@Getter
@Setter
@NoArgsConstructor
public class Schema {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "org_id", nullable = false)
    private UUID orgId;

    @Column(name = "datasource_id", nullable = false)
    private UUID datasourceId;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_enabled", nullable = false)
    private EnableStatus isEnabled = EnableStatus.ENABLED;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        isEnabled = EnableStatus.DISABLED;
    }

    public void updateNameAndStatus(String name, EnableStatus status) {
        if (name != null) {
            this.name = name;
        }
        if (status != null) {
            this.isEnabled = status;
        }
    }

    private Schema(UUID orgId, UUID datasourceId, UUID createdBy, String name) {
        this.orgId = orgId;
        this.datasourceId = datasourceId;
        this.createdBy = createdBy;
        this.name = name;
        this.isEnabled = EnableStatus.ENABLED;
    }

    public static Schema create(UUID orgId, UUID datasourceId, UUID createdBy, String name) {
        return new Schema(orgId, datasourceId, createdBy, name);
    }
}