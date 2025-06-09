package com.pandaterry.gateway.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class TestController {
    @GetMapping("/test")
    public Mono<String> test() {
        return Mono.just("test");
    }

    @GetMapping("/queries/test")
    public Mono<String> queries() {
        return Mono.just("queries");
    }

    @GetMapping("/agents/test")
    public Mono<String> agents() {
        return Mono.just("agents");
    }

    @GetMapping("/jobs/test")
    public Mono<String> jobs() {
        return Mono.just("jobs");
    }

    @GetMapping("/datasources/test")
    public Mono<String> datasources() {
        return Mono.just("datasources");
    }
}