package com.pandaterry.presentation.controller;

import com.pandaterry.presentation.dto.request.DatabaseConnectionRequest;
import com.pandaterry.presentation.dto.response.DatabaseConnectionResponse;
import com.pandaterry.application.service.database.DatabaseService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;

@Controller("/api/v1/datasources")
public class DatabaseController {

    @Inject
    private DatabaseService databaseService;

    // 디비 연결 확인
    @Post("/test")
    public HttpResponse<DatabaseConnectionResponse> testConnection(DatabaseConnectionRequest req){
        DatabaseConnectionResponse response = databaseService.testConnect(req);
        return HttpResponse.ok(response);
    }

}
