package com.pandaterry.schema_microservice.presentation.controller;

import com.pandaterry.msa_contracts.constants.RoutePath;
import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.dto.schema.response.DatasourceResponse;
import com.pandaterry.msa_contracts.dto.schema.request.DatasourceUpdateRequest;
import com.pandaterry.schema_microservice.application.service.DatasourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(RoutePath.Datasource.BASE)
@RequiredArgsConstructor
public class DatasourceController {
    private final DatasourceService datasourceService;

    // UUID 할당을 위해 빈 데이터소스를 생성
    @PostMapping
    public ResponseEntity<DatasourceResponse> initDatasource(
            @RequestHeader(HeaderKeys.ORG_ID) String orgId,
            @RequestHeader(HeaderKeys.USER_ID) String userId,
            @RequestHeader(HeaderKeys.AGENT_ID) String agentId) {
        return ResponseEntity.ok(datasourceService.initDatasource(orgId, userId, agentId));
    }

    // 실제로 활성화단계에서 이름, 디비 종류 등을 설정하여 활성화
    @PutMapping(RoutePath.Datasource.DETAIL_SUFFIX)
    public ResponseEntity<DatasourceResponse> activateDatasource(
            @PathVariable String datasourceId,
            @RequestBody DatasourceUpdateRequest request,
            @RequestHeader(HeaderKeys.ORG_ID) String orgId,
            @RequestHeader(HeaderKeys.USER_ID) String userId,
            @RequestHeader(HeaderKeys.AGENT_ID) String agentId) {
        return ResponseEntity.ok(datasourceService.activateDatasource(datasourceId, orgId, userId, agentId, request));
    }

    @GetMapping
    public ResponseEntity<List<DatasourceResponse>> getDatasources(
            @RequestHeader(HeaderKeys.ORG_ID) String orgId) {
        return ResponseEntity.ok(datasourceService.getDatasources(orgId));
    }

    @GetMapping(RoutePath.Datasource.DETAIL_SUFFIX)
    public ResponseEntity<DatasourceResponse> getDatasource(
            @PathVariable String datasourceId) {
        return ResponseEntity.ok(datasourceService.getDatasource(datasourceId));
    }

    @DeleteMapping(RoutePath.Datasource.DETAIL_SUFFIX)
    public ResponseEntity<Void> deactivateDatasource(
            @PathVariable String datasourceId) {
        datasourceService.deactivateDatasource(datasourceId);
        return ResponseEntity.ok().build();
    }
}