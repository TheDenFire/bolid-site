package com.bolid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BolidApplication {
    public static void main(String[] args) {
        SpringApplication.run(BolidApplication.class, args);
    }
} 