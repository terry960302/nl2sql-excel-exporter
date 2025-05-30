package com.pandaterry.schema_microservice.domain.entity;

import com.pandaterry.schema_microservice.domain.enumerated.DatabaseEngineType;
import com.pandaterry.schema_microservice.domain.enumerated.DatabaseType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;

import java.time.LocalDateTime;
import java.util.Map;
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
    @Column(nullable = false)
    private DatabaseType dbType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DatabaseEngineType engineType;

    @Column(nullable = false)
    private String endpoint;

    @Column(nullable = false)
    private String username;

    @Column(name = "password_encrypted", nullable = false)
    private String passwordEncrypted;

    @Column(name = "ssl_enabled")
    private boolean sslEnabled;

    @Column(nullable = true)
    private String connectionString;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> options;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_enabled", nullable = false)
    private EnableStatus isEnabled = EnableStatus.ENABLED;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private Datasource(UUID orgId, String name, DatabaseType dbType, DatabaseEngineType engineType, String endpoint, String username,
            String password, boolean sslEnabled, Map<String, Object> options) {
        this.orgId = orgId;
        this.name = name;
        this.dbType = dbType;
        this.engineType = engineType;
        this.endpoint = endpoint;
        this.username = username;
        this.passwordEncrypted = password;
        this.sslEnabled = sslEnabled;
        this.options = options;
    }

    public static Datasource create(UUID orgId, String name, DatabaseType dbType, DatabaseEngineType engineType, String endpoint,
            String username, String password, boolean sslEnabled, Map<String, Object> options) {
        return new Datasource(orgId, name, dbType, engineType, endpoint, username, password, sslEnabled, options);
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

    public void deactivate() {
        isEnabled = EnableStatus.DISABLED;
    }


    public void updateName(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    public void updateEndpoint(String endpoint) {
        if (endpoint != null) {
            this.endpoint = endpoint;
        }
    }

    public void updateUsername(String username) {
        if (username != null) {
            this.username = username;
        }
    }

    public void updatePassword(String password) {
        if (password != null) {
            this.passwordEncrypted = password;
        }
    }

    public void updateSslEnabled(boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }

    public void updateOptions(Map<String, Object> options) {
        if (options != null) {
            this.options = options;
        }
    }

}