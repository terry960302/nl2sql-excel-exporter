package com.pandaterry.gateway.shared.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(-2) // Spring 기본 GlobalErrorHandler보다 우선순위 높게
public class GlobalExceptionHandler implements WebExceptionHandler {

    @SneakyThrows
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String code = ErrorCode.UNKNOWN_ERROR.getCode();
        String message = ex.getMessage();

        if (ex instanceof GatewayException ge) {
            httpStatus = HttpStatus.valueOf(ge.getErrorCode().getStatus());
            code = ge.getErrorCode().getCode();
            message = ge.getErrorCode().getMessage();
        }

        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> response = new HashMap<>();
        response.put("status", httpStatus.value());
        response.put("code", code);
        response.put("message", message);

        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(response);


        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(body.getBytes(StandardCharsets.UTF_8))));
    }
}
