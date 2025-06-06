package com.pandaterry.presentation.controller;

import com.pandaterry.application.facade.QueryJobProcessFacade;
import com.pandaterry.msa_contracts.dto.query.request.NaturalLanguageQueryRequest;
import com.pandaterry.msa_contracts.dto.query.response.NaturalLanguageQueryResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;

@Controller("/api/v1/queries")
public class QueryController {

    @Inject
    private QueryJobProcessFacade queryJobProcessFacade;

    @Post("/execute")
    public HttpResponse<NaturalLanguageQueryResponse> executeNaturalLanguageQuery(NaturalLanguageQueryRequest request){
        return HttpResponse.ok(queryJobProcessFacade.handleQuery(request));
    }
}
