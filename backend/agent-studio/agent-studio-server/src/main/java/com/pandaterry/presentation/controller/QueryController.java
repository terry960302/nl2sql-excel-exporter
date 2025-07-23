package com.pandaterry.presentation.controller;

import com.pandaterry.application.facade.QueryJobProcessFacade;
import com.pandaterry.infrastructure.client.DownloadClient;
import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.constants.RoutePath;
import com.pandaterry.msa_contracts.dto.query.request.NaturalLanguageQueryRequest;
import com.pandaterry.msa_contracts.dto.query.response.NaturalLanguageQueryResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;

import java.io.InputStream;

@Controller(RoutePath.Query.BASE)
public class QueryController {

    @Inject
    private QueryJobProcessFacade queryJobProcessFacade;

    @Post(RoutePath.Query.EXECUTE_SUFFIX)
    public HttpResponse<NaturalLanguageQueryResponse> executeNaturalLanguageQuery(@Header(HeaderKeys.AUTHORIZATION) String authorization,
                                                                                  @Body NaturalLanguageQueryRequest request) {
        return HttpResponse.ok(queryJobProcessFacade.handleQuery(authorization, request));
    }
}
