package com.pandaterry.quota_microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class QuotaMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuotaMicroserviceApplication.class, args);
	}

}
