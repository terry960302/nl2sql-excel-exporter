package com.pandaterry.schema_microservice.presentation.controller;

import com.pandaterry.schema_microservice.presentation.dto.DatasourceCreateRequest;
import com.pandaterry.schema_microservice.presentation.dto.DatasourceResponse;
import com.pandaterry.schema_microservice.presentation.dto.DatasourceUpdateRequest;
import com.pandaterry.schema_microservice.application.service.DatasourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/datasources")
@RequiredArgsConstructor
public class DatasourceController {
    private final DatasourceService datasourceService;

    @PostMapping
    public ResponseEntity<DatasourceResponse> createDatasource(
            @RequestBody DatasourceCreateRequest request,
            @RequestHeader("X-Organization-Id") UUID orgId) {
        return ResponseEntity.ok(datasourceService.createDatasource(orgId, request));
    }

    @GetMapping
    public ResponseEntity<List<DatasourceResponse>> getDatasources(
            @RequestHeader("X-Organization-Id") UUID orgId) {
        return ResponseEntity.ok(datasourceService.getDatasources(orgId));
    }

    @GetMapping("/{datasourceId}")
    public ResponseEntity<DatasourceResponse> getDatasource(
            @PathVariable UUID datasourceId) {
        return ResponseEntity.ok(datasourceService.getDatasource(datasourceId));
    }

    @PutMapping("/{datasourceId}")
    public ResponseEntity<DatasourceResponse> updateDatasource(
            @PathVariable UUID datasourceId,
            @RequestBody DatasourceUpdateRequest request) {
        return ResponseEntity.ok(datasourceService.updateDatasource(datasourceId, request));
    }

    @DeleteMapping("/{datasourceId}")
    public ResponseEntity<Void> deactivateDatasource(
            @PathVariable UUID datasourceId) {
        datasourceService.deactivateDatasource(datasourceId);
        return ResponseEntity.ok().build();
    }
}