package com.internship.tool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration // Marks this class as Spring configuration
public class AppConfig {

    // Creates PasswordEncoder bean
    @Bean
    public PasswordEncoder passwordEncoder() {

        // BCrypt password hashing
        return new BCryptPasswordEncoder();
    }
}