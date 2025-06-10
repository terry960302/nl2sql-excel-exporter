package com.pandaterry.presentation.controller;

import com.pandaterry.application.facade.QueryJobProcessFacade;
import com.pandaterry.infrastructure.client.DownloadClient;
import com.pandaterry.msa_contracts.constants.RoutePath;
import com.pandaterry.msa_contracts.dto.query.request.NaturalLanguageQueryRequest;
import com.pandaterry.msa_contracts.dto.query.response.NaturalLanguageQueryResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.server.types.files.StreamedFile;
import jakarta.inject.Inject;

@Controller("/api/v1/" + RoutePath.Query.BASE)
public class QueryController {

    @Inject
    private QueryJobProcessFacade queryJobProcessFacade;

    @Inject
    private DownloadClient downloadClient;

    @Post(RoutePath.Query.EXECUTE_SUFFIX)
    public HttpResponse<NaturalLanguageQueryResponse> executeNaturalLanguageQuery(NaturalLanguageQueryRequest request){
        return HttpResponse.ok(queryJobProcessFacade.handleQuery(request));
    }

    @Post(RoutePath.Query.EXECUTE_SUFFIX + "/download")
    public HttpResponse<StreamedFile> downloadExcel(NaturalLanguageQueryRequest request) throws Exception {
        NaturalLanguageQueryResponse res = queryJobProcessFacade.handleQuery(request);
        StreamedFile file = new StreamedFile(
                downloadClient.download(res.downloadUrl()),
                MediaType.of("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        ).attach(res.filename());
        return HttpResponse.ok(file);
    }
}
