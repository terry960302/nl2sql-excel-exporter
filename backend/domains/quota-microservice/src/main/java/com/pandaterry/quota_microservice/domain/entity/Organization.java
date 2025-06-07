package com.pandaterry.quota_microservice.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.util.UUID;

@Entity
@Table(name = "organizations")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Organization {

    @Id
    private UUID id;

    @Column(name = "plan_id", nullable = false)
    private UUID planId;

    public static Organization create(UUID id, UUID planId) {
        Organization org = new Organization();
        org.id = id;
        org.planId = planId;
        return org;
    }
}
