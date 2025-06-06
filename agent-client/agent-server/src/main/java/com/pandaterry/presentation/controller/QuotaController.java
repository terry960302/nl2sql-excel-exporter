package com.pandaterry.presentation.controller;

import com.pandaterry.infrastructure.client.QuotaServiceClient;
import io.micronaut.http.annotation.Controller;
import jakarta.inject.Inject;

@Controller("/api/v1/quota")
public class QuotaController {
    @Inject
    private QuotaServiceClient quotaServiceClient;
}
