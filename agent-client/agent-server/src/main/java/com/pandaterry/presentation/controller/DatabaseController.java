package com.pandaterry.presentation.controller;

import com.pandaterry.application.dto.request.DatabaseConnectionRequest;
import com.pandaterry.application.dto.response.DatabaseConnectionResponse;
import com.pandaterry.application.service.database.DatabaseService;
import com.pandaterry.domain.enums.DatabaseType;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

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
