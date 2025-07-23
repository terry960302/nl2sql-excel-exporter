package com.pandaterry.infrastructure.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Replaces;
import jakarta.inject.Named;

@Factory
public class JacksonConfig {

    @Bean
    @Primary
    @Named("jacksonMapper")
    public com.fasterxml.jackson.databind.ObjectMapper objectMapper(){
        ObjectMapper mapper = new ObjectMapper();

        // 기본 권장 설정
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // Java 8 Time 지원
        mapper.registerModule(new JavaTimeModule());

//        // null 값 무시
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return mapper;    }
}
