package com.pandaterry.query_microservice.presentation.controller;

import com.pandaterry.query_microservice.domain.exception.QueryException;
import com.pandaterry.query_microservice.application.service.QueryService;
import com.pandaterry.query_microservice.application.dto.request.NaturalLanguageQueryRequest;
import com.pandaterry.query_microservice.application.service.NaturalLanguageQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/query")
@RequiredArgsConstructor
public class QueryController {
    private final QueryService queryService;
    private final NaturalLanguageQueryService naturalLanguageQueryService;

    @PostMapping("/natural-to-sql")
    public Mono<ResponseEntity<String>> convertNaturalLanguageToSQL(
            @RequestBody NaturalLanguageQueryRequest request) {
        return naturalLanguageQueryService.convertToSQL(request)
                .map(sql -> ResponseEntity.ok(sql))
                .onErrorResume(e -> {
                    if (e instanceof QueryException) {
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(((QueryException) e).getMessage()));
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("서버 내부 오류가 발생했습니다."));
                });
    }
}