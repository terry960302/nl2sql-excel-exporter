package com.pandaterry.schema_microservice.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import com.pandaterry.msa_contracts.enums.schema.EnableStatus;

@Entity
@Table(name = "table_definitions")
@Getter
@NoArgsConstructor
public class TableDefinition {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(name = "schema_id", nullable = false)
    private UUID schemaId;

    @Column(name = "table_name", nullable = false)
    private String tableName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static TableDefinition create(UUID schemaId, String tableName) {
        TableDefinition table = new TableDefinition();
        table.schemaId = schemaId;
        table.tableName = tableName;
        return table;
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