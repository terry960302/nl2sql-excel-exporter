package com.pandaterry.schema_microservice.presentation.controller;

import com.pandaterry.schema_microservice.presentation.dto.TableDefinitionResponse;
import com.pandaterry.schema_microservice.application.service.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/schemas/{schemaId}/tables")
@RequiredArgsConstructor
public class TableController {
    private final TableService tableService;

    @GetMapping
    public ResponseEntity<List<TableDefinitionResponse>> getTablesBySchema(
            @PathVariable UUID schemaId) {
        return ResponseEntity.ok(tableService.getTablesBySchema(schemaId));
    }

    @GetMapping("/{tableId}")
    public ResponseEntity<TableDefinitionResponse> getTable(
            @PathVariable UUID schemaId,
            @PathVariable UUID tableId) {
        return ResponseEntity.ok(tableService.getTable(tableId));
    }

    @DeleteMapping("/{tableId}")
    public ResponseEntity<Void> deactivateTable(
            @PathVariable UUID schemaId,
            @PathVariable UUID tableId) {
        tableService.deactivateTable(tableId);
        return ResponseEntity.ok().build();
    }
}