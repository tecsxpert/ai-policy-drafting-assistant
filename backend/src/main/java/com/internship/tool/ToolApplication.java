package com.internship.tool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication // Main Spring Boot configuration annotation
@EnableJpaAuditing // Enables JPA auditing (for createdAt & updatedAt)
@EnableCaching // Enables caching in application
@EnableScheduling // VERY IMPORTANT
public class ToolApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToolApplication.class, args); // Starts the application
    }
}