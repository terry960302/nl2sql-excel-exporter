package com.pandaterry.schema_microservice.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

import com.pandaterry.msa_contracts.enums.schema.EnableStatus;

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


    @Column(nullable = false)
    private String name;

    @Column(name = "raw_json", nullable = false, length = 3000)
    private String rawJson;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private Schema(UUID orgId, UUID datasourceId, UUID createdBy, String name, String rawJson) {
        this.orgId = orgId;
        this.datasourceId = datasourceId;
        this.createdBy = createdBy;
        this.name = name;
        this.rawJson = rawJson;
    }

    public static Schema create(UUID orgId, UUID datasourceId, UUID createdBy, String name, String rawJson) {
        return new Schema(orgId, datasourceId, createdBy, name, rawJson);
    }
}