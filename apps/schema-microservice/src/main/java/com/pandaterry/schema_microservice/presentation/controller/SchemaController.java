package com.pandaterry.schema_microservice.presentation.controller;

import com.pandaterry.schema_microservice.presentation.dto.SchemaCreateRequest;
import com.pandaterry.schema_microservice.presentation.dto.SchemaResponse;
import com.pandaterry.schema_microservice.presentation.dto.SchemaUpdateRequest;
import com.pandaterry.schema_microservice.application.service.SchemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/schemas")
@RequiredArgsConstructor
public class SchemaController {
    private final SchemaService schemaService;

    @PostMapping
    public ResponseEntity<SchemaResponse> createSchema(
            @RequestBody SchemaCreateRequest request,
            @RequestHeader("X-Organization-Id") UUID orgId,
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(schemaService.createSchema(request, orgId, userId));
    }

    @GetMapping("/datasource/{datasourceId}")
    public ResponseEntity<List<SchemaResponse>> getSchemasByDatasource(
            @PathVariable UUID datasourceId) {
        return ResponseEntity.ok(schemaService.getSchemasByDatasource(datasourceId));
    }

    @GetMapping("/{schemaId}")
    public ResponseEntity<SchemaResponse> getSchema(
            @PathVariable UUID schemaId) {
        return ResponseEntity.ok(schemaService.getSchema(schemaId));
    }

    @PutMapping("/{schemaId}")
    public ResponseEntity<SchemaResponse> updateSchema(
            @PathVariable UUID schemaId,
            @RequestBody SchemaUpdateRequest request) {
        return ResponseEntity.ok(schemaService.updateSchema(schemaId, request));
    }

    @DeleteMapping("/{schemaId}")
    public ResponseEntity<Void> deactivateSchema(
            @PathVariable UUID schemaId) {
        schemaService.deactivateSchema(schemaId);
        return ResponseEntity.ok().build();
    }
}