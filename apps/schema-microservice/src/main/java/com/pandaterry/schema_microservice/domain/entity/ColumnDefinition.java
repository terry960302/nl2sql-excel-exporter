package com.pandaterry.schema_microservice.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;

@Entity
@Table(name = "column_definitions")
@Getter
@NoArgsConstructor
public class ColumnDefinition {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(name = "table_id", nullable = false)
    private UUID tableId;

    @Column(name = "column_name", nullable = false)
    private String columnName;

    @Column(name = "data_type", nullable = false)
    private String dataType;

    @Column(name = "is_nullable", nullable = false)
    private boolean isNullable;

    @Column(name = "is_primary_key", nullable = false)
    private boolean isPrimaryKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_enabled", nullable = false)
    private EnableStatus isEnabled = EnableStatus.ENABLED;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static ColumnDefinition create(UUID tableId, String columnName, String dataType,
                                        boolean isNullable, boolean isPrimaryKey) {
        ColumnDefinition column = new ColumnDefinition();
        column.tableId = tableId;
        column.columnName = columnName;
        column.dataType = dataType;
        column.isNullable = isNullable;
        column.isPrimaryKey = isPrimaryKey;
        return column;
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