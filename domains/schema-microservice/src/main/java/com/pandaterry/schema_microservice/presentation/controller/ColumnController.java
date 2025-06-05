package com.pandaterry.schema_microservice.presentation.controller;

import com.pandaterry.schema_microservice.presentation.dto.ColumnDefinitionResponse;
import com.pandaterry.schema_microservice.application.service.ColumnService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Deprecated
@RestController
@RequestMapping("/tables/{tableId}/columns")
@RequiredArgsConstructor
public class ColumnController {
    private final ColumnService columnService;

    @GetMapping
    public ResponseEntity<List<ColumnDefinitionResponse>> getColumnsByTable(
            @PathVariable UUID tableId) {
        return ResponseEntity.ok(columnService.getColumnsByTable(tableId));
    }

    @GetMapping("/{columnId}")
    public ResponseEntity<ColumnDefinitionResponse> getColumn(
            @PathVariable UUID tableId,
            @PathVariable UUID columnId) {
        return ResponseEntity.ok(columnService.getColumn(columnId));
    }

    @DeleteMapping("/{columnId}")
    public ResponseEntity<Void> deactivateColumn(
            @PathVariable UUID tableId,
            @PathVariable UUID columnId) {
        columnService.deactivateColumn(columnId);
        return ResponseEntity.ok().build();
    }
}