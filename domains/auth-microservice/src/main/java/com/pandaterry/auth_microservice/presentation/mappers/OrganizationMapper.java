package com.pandaterry.auth_microservice.presentation.mappers;

import com.pandaterry.auth_microservice.domain.entity.Organization;
import com.pandaterry.msa_contracts.dto.auth.response.OrganizationResponse;

public class OrganizationMapper {
    public static OrganizationResponse toResponse(Organization organization) {
        return OrganizationResponse.builder()
                .id(organization.getId().toString())
                .name(organization.getName())
                .displayName(organization.getDisplayName())
                .planName(organization.getPlan().getName())
                .createdAt(organization.getCreatedAt())
                .build();
    }
}
