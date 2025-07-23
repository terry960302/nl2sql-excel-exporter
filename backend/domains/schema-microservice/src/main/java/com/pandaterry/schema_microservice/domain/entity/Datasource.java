package com.pandaterry.schema_microservice.domain.entity;

import com.pandaterry.msa_contracts.enums.schema.DatabaseEngineType;
import com.pandaterry.msa_contracts.enums.schema.DatabaseType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.pandaterry.msa_contracts.enums.schema.EnableStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "datasources")
@Getter
@Setter
@NoArgsConstructor
public class Datasource {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "org_id", nullable = false)
    private UUID orgId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private DatabaseType dbType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private DatabaseEngineType engineType;

    @Column(nullable = true)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_enabled", nullable = false)
    private EnableStatus isEnabled = EnableStatus.DISABLED;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by_user")
    private UUID createdByUser;

    @Column(name = "created_by_agent")
    private UUID createdByAgent;

    private Datasource(UUID orgId, String name, DatabaseType dbType, DatabaseEngineType engineType, UUID userId, UUID agentId) {
        this.orgId = orgId;
        this.name = name;
        this.dbType = dbType;
        this.engineType = engineType;
        this.createdByUser = userId;
        this.createdByAgent = agentId;
    }

    public static Datasource create(UUID orgId, String name, DatabaseType dbType, DatabaseEngineType engineType, UUID userId, UUID agentId) {
        return new Datasource(orgId, name, dbType, engineType, userId, agentId);
    }

    public static Datasource init(UUID orgId, UUID userId, UUID agentId) {
        String initialName = "datasource-" + LocalDateTime.now();
        return new Datasource(orgId, initialName, null, null, userId, agentId);
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

    public void activate(){
        isEnabled = EnableStatus.ENABLED;
    }
    public void deactivate() {
        isEnabled = EnableStatus.DISABLED;
    }


    public void updateName(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    public void updateDbType(DatabaseType dbType) {
        if (dbType != null) {
            this.dbType = dbType;
        }
    }

    public void updateEngineType(DatabaseEngineType engineType) {
        if (engineType != null) {  
            this.engineType = engineType;
        }
    }

    public void updateDescription(String description){
        if(description == null) return;
        this.description = description;
    }
}