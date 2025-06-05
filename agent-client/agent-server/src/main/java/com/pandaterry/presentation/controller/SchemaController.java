package com.pandaterry.presentation.controller;

import com.pandaterry.msa_contracts.dto.schema.request.RegisterSchemaRequest;
import com.pandaterry.msa_contracts.dto.schema.response.RegisterSchemaResponse;
import com.pandaterry.presentation.dto.request.ScanSchemaRequest;
import com.pandaterry.presentation.dto.response.ScanSchemaResponse;
import com.pandaterry.application.service.database.DatabaseService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;

@Controller("/api/v1/schemas")
public class SchemaController {

    @Inject
    private DatabaseService databaseService;

    @Post("/scan")
    public HttpResponse<ScanSchemaResponse> scanSchema(ScanSchemaRequest request){
        ScanSchemaResponse res = databaseService.scanSchema(request);
        return HttpResponse.ok(res);
    }

    @Post()
    public HttpResponse<RegisterSchemaResponse> registerSchema(RegisterSchemaRequest request){
        RegisterSchemaResponse res = databaseService.registerSchema(request);
        return HttpResponse.ok(res);
    }
}
