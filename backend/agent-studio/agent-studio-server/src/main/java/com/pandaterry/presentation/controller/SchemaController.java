package com.pandaterry.presentation.controller;

import com.pandaterry.msa_contracts.constants.RoutePath;
import com.pandaterry.msa_contracts.dto.schema.request.RegisterSchemaRequest;
import com.pandaterry.msa_contracts.dto.schema.response.RegisterSchemaResponse;
import com.pandaterry.presentation.dto.request.ScanSchemaRequest;
import com.pandaterry.presentation.dto.response.ScanSchemaResponse;
import com.pandaterry.application.service.database.DatabaseService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;

@Controller("/api/v1/" + RoutePath.Schema.BASE)
public class SchemaController {

    @Inject
    private DatabaseService databaseService;

    @Post(RoutePath.Schema.SCAN_SUFFIX)
    public HttpResponse<ScanSchemaResponse> scanSchema(ScanSchemaRequest request){
        ScanSchemaResponse res = databaseService.scanSchema(request);
        return HttpResponse.ok(res);
    }

    @Post(RoutePath.Schema.REGISTER_SUFFIX)
    public HttpResponse<RegisterSchemaResponse> registerSchema(RegisterSchemaRequest request){
        RegisterSchemaResponse res = databaseService.registerSchema(request);
        return HttpResponse.ok(res);
    }
}
