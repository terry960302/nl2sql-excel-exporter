package com.pandaterry.quota_microservice.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class QuotaUsersPageResponse {
    private final List<QuotaUsersItem> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
}
