package com.pandaterry.schema_microservice.presentation.controller;

import com.pandaterry.schema_microservice.presentation.dto.AliasCreateRequest;
import com.pandaterry.schema_microservice.presentation.dto.AliasResponse;
import com.pandaterry.schema_microservice.presentation.dto.AliasUpdateRequest;
import com.pandaterry.schema_microservice.application.service.AliasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/columns/{columnId}/aliases")
@RequiredArgsConstructor
public class AliasController {
    private final AliasService aliasService;

    @GetMapping
    public ResponseEntity<List<AliasResponse>> getAliasesByColumn(
            @PathVariable UUID columnId) {
        return ResponseEntity.ok(aliasService.getAliasesByColumn(columnId));
    }

    @GetMapping("/{aliasId}")
    public ResponseEntity<AliasResponse> getAlias(
            @PathVariable UUID columnId,
            @PathVariable UUID aliasId) {
        return ResponseEntity.ok(aliasService.getAlias(aliasId));
    }

    @PostMapping
    public ResponseEntity<AliasResponse> createAlias(
            @PathVariable UUID columnId,
            @Valid @RequestBody AliasCreateRequest request) {
        return ResponseEntity.ok(aliasService.createAlias(request));
    }

    @PutMapping("/{aliasId}")
    public ResponseEntity<AliasResponse> updateAlias(
            @PathVariable UUID columnId,
            @PathVariable UUID aliasId,
            @Valid @RequestBody AliasUpdateRequest request) {
        return ResponseEntity.ok(aliasService.updateAlias(aliasId, request));
    }

    @DeleteMapping("/{aliasId}")
    public ResponseEntity<Void> deactivateAlias(
            @PathVariable UUID columnId,
            @PathVariable UUID aliasId) {
        aliasService.deactivateAlias(aliasId);
        return ResponseEntity.ok().build();
    }
}