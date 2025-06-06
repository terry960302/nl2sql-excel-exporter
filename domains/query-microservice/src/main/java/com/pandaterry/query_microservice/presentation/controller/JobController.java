package com.pandaterry.query_microservice.presentation.controller;

import com.pandaterry.msa_contracts.constants.ApiPath;
import com.pandaterry.msa_contracts.dto.query.request.JobResultRequest;
import com.pandaterry.query_microservice.application.service.JobService;
import com.pandaterry.query_microservice.domain.model.ExecutionJob;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/v1" + ApiPath.Job.BASE)
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    @GetMapping
    public Mono<ResponseEntity<ExecutionJob>> pollJob(@RequestParam UUID agentId,
                                                      @RequestParam(required = false) String status) {
        return jobService.pollPendingJob(agentId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @PostMapping(ApiPath.Job.DETAIL_RESULT)
    public Mono<ResponseEntity<Void>> reportResult(@PathVariable UUID jobId,
                                                   @RequestBody JobResultRequest request) {
        return jobService.reportResult(jobId, request)
                .thenReturn(ResponseEntity.ok().build());
    }
}
