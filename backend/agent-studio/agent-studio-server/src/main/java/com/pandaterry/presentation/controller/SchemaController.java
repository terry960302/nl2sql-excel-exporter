package com.pandaterry.presentation.controller;

import com.pandaterry.infrastructure.client.DefaultSchemaClient;
import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.constants.RoutePath;
import com.pandaterry.msa_contracts.dto.schema.request.RegisterSchemaRequest;
import com.pandaterry.msa_contracts.dto.schema.response.RegisterSchemaResponse;
import com.pandaterry.presentation.dto.request.ScanSchemaRequest;
import com.pandaterry.presentation.dto.response.ScanSchemaResponse;
import com.pandaterry.application.service.datasource.DatasourceService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;

@Controller(RoutePath.Schema.BASE)
public class SchemaController {

    @Inject
    private DatasourceService databaseService;
    @Inject
    DefaultSchemaClient schemaClient;

    @Post(RoutePath.Schema.SCAN_SUFFIX)
    public HttpResponse<ScanSchemaResponse> scanSchema(@Body ScanSchemaRequest request) {
        ScanSchemaResponse res = databaseService.scanSchema(request);
        return HttpResponse.ok(res);
    }

    @Post(RoutePath.Schema.REGISTER_SUFFIX)
    public HttpResponse<RegisterSchemaResponse> registerSchema(@Header(HeaderKeys.AUTHORIZATION) String authorization,
                                                               @Body RegisterSchemaRequest request) {
        RegisterSchemaResponse res = schemaClient.uploadSchema(authorization, request);
        return HttpResponse.ok(res);
    }
}
