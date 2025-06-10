package com.pandaterry.gateway.presentation;

import com.pandaterry.gateway.shared.exceptions.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    @RequestMapping("/fallback")
    public Mono<ResponseEntity<String>> fallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorCode.SERVICE_UNAVAILABLE.getMessage()));
    }
}
