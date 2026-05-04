package com.internship.tool.config; 
// Package must match folder: src/main/java/com/internship/tool/config

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration 
// Marks this class as a configuration class for Spring Boot

@EnableScheduling 
// Enables scheduling support (@Scheduled will work)
public class SchedulerConfig {

    // No methods required here
    // This class only enables scheduling feature globally

}