package com.pandaterry.schema_microservice.presentation.controller;

import com.pandaterry.msa_contracts.dto.schema.request.RegisterSchemaRequest;
import com.pandaterry.msa_contracts.dto.schema.response.RegisterSchemaResponse;
import com.pandaterry.schema_microservice.application.service.SchemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/v1/schemas")
@RequiredArgsConstructor
public class SchemaController {
    private final SchemaService schemaService;

    @PostMapping
    public ResponseEntity<RegisterSchemaResponse> uploadSchema(
            @RequestHeader("X-Organization-Id") UUID orgId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-Agent-Id") UUID agentId,
            @RequestBody RegisterSchemaRequest req){
        return ResponseEntity.ok(schemaService.uploadSchema(orgId, userId, agentId, req));
    }
}