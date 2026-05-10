package com.internship.tool.service;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AiServiceClient {

    // Simulates AI response (can replace with real API later)
    public Map<String, Object> generateReport(String input) {

        Map<String, Object> response = new HashMap<>();

        // Dummy AI logic (for now)
        String result = "AI Analysis: " + input;

        response.put("data", result);

        return response;
    }
}