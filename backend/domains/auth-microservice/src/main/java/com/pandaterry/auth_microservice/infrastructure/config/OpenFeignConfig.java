package com.pandaterry.auth_microservice.infrastructure.config;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignClientProperties;
//import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FeignClientProperties.class)
public class OpenFeignConfig {

//    @Bean
//    public FeignContext feignContext() {
//        return new FeignContext();
//    }
}