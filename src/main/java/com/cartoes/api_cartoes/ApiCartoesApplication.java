package com.cartoes.api_cartoes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class ApiCartoesApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiCartoesApplication.class, args);
    }
}
