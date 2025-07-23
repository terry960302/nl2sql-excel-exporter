package com.pandaterry.presentation.controller;

import com.pandaterry.infrastructure.client.DownloadClient;
import com.pandaterry.msa_contracts.constants.RoutePath;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.server.types.files.StreamedFile;
import jakarta.inject.Inject;

import java.io.InputStream;

@Controller(RoutePath.File.BASE)
public class FileController {

    @Inject
    private DownloadClient downloadClient;

    @Post(RoutePath.File.DOWNLOAD_SUFFIX)
    public HttpResponse<StreamedFile> download(@PathVariable String filename) throws Exception {
        return HttpResponse.ok(toStreamFile(downloadClient.download(filename), filename));
    }

    public StreamedFile toStreamFile(InputStream inputStream, String filename) {
        final String EXCEL_MEDIA_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        return new StreamedFile(
                inputStream,
                MediaType.of(EXCEL_MEDIA_TYPE)
        ).attach(filename);
    }
}
