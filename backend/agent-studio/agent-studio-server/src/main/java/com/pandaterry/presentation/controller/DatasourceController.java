package com.pandaterry.presentation.controller;

import com.pandaterry.infrastructure.client.DefaultDatasourceClient;
import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.constants.RoutePath;
import com.pandaterry.msa_contracts.dto.schema.response.DatasourceResponse;
import com.pandaterry.presentation.dto.request.DatabaseConnectionRequest;
import com.pandaterry.presentation.dto.response.DatabaseConnectionResponse;
import com.pandaterry.application.service.datasource.DatasourceService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;

import java.util.List;


@Controller(RoutePath.Datasource.BASE)
public class DatasourceController {

    @Inject
    private DatasourceService databaseService;

    @Inject
    private DefaultDatasourceClient datasourceClient;

    // 디비 연결 확인
    @Post(RoutePath.Datasource.TEST_SUFFIX)
    public HttpResponse<DatabaseConnectionResponse> testConnection(@Body DatabaseConnectionRequest req) {
        DatabaseConnectionResponse response = databaseService.testConnect(req);
        return HttpResponse.ok(response);
    }

    // 데이터소스 목록 가져오기
    @Get
    public HttpResponse<List<DatasourceResponse>> getDatasources(@Header(HeaderKeys.AUTHORIZATION) String authorization) {
        List<DatasourceResponse> datasource = datasourceClient.getDatasources(authorization);
        return HttpResponse.ok(datasource);
    }

}
