package com.pandaterry.query_microservice.presentation.controller;

import com.pandaterry.query_microservice.domain.exception.QueryException;
import com.pandaterry.query_microservice.application.service.QueryService;
import com.pandaterry.query_microservice.application.dto.request.NaturalLanguageQueryRequest;
import com.pandaterry.query_microservice.domain.model.ExecutionJob;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/queries")
@RequiredArgsConstructor
public class QueryController {
    private final QueryService queryService;

    @PostMapping
    public Mono<ResponseEntity<ExecutionJob>> requestQuery(
            @RequestBody NaturalLanguageQueryRequest request) {
        return queryService.createQueryJob(request)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    if (e instanceof QueryException) {
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(null));
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(null));
                });
    }
}