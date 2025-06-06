package com.pandaterry.schema_microservice.presentation.controller;

import com.pandaterry.msa_contracts.constants.ApiPath;
import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.dto.schema.request.RegisterSchemaRequest;
import com.pandaterry.msa_contracts.dto.schema.response.RegisterSchemaResponse;
import com.pandaterry.schema_microservice.application.service.SchemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/v1" + ApiPath.Schema.BASE)
@RequiredArgsConstructor
public class SchemaController {
    private final SchemaService schemaService;

    @PostMapping
    public ResponseEntity<RegisterSchemaResponse> uploadSchema(
            @RequestHeader(HeaderKeys.ORG_ID) UUID orgId,
            @RequestHeader(HeaderKeys.USER_ID) UUID userId,
            @RequestHeader(HeaderKeys.AGENT_ID) UUID agentId,
            @RequestBody RegisterSchemaRequest req){
        return ResponseEntity.ok(schemaService.uploadSchema(orgId, userId, agentId, req));
    }
}