package com.pandaterry.auth_microservice.presentation.dto;

import java.time.LocalDateTime;

import com.pandaterry.auth_microservice.domain.entity.Organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationResponse {
    private String id;
    private String name;
    private String displayName;
    private String planName;
    private LocalDateTime createdAt;

    public static OrganizationResponse of(Organization organization) {
        return OrganizationResponse.builder()
                .id(organization.getId().toString())
                .name(organization.getName())
                .displayName(organization.getDisplayName())
                .planName(organization.getPlan().getName())
                .createdAt(organization.getCreatedAt())
                .build();
    }
}
