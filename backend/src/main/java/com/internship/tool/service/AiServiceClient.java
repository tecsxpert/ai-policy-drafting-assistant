package com.internship.tool.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AiServiceClient — Bridge between Spring Boot backend and Flask AI service.
 * 
 * AI Developer 3 — Enhanced with:
 * - Configurable AI service URL via application.yml
 * - 10-second connect + read timeout
 * - Graceful null return on error (AI down ≠ app crash)
 * - Proper SLF4J logging (no PII in logs)
 * - Generic method to call any Flask AI endpoint
 */
@Service
public class AiServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(AiServiceClient.class);

    // Reads AI_SERVICE_URL from application.yml (default: http://localhost:5000)
    @Value("${ai.service.url:http://localhost:5000}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate;

    public AiServiceClient() {
        // 10 second timeout — if AI takes longer, return null gracefully
        org.springframework.http.client.SimpleClientHttpRequestFactory factory =
            new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(10000);
        this.restTemplate = new RestTemplate(factory);
    }

    /**
     * Generic method to call any Flask AI endpoint.
     * Used by PolicyService to call /describe, /recommend, /categorise, etc.
     *
     * @param path      The endpoint path (e.g., "/api/describe")
     * @param inputText The text input to send to the AI
     * @return          Map containing the AI response, or null on error
     */
    public Map callAiEndpoint(String path, String inputText) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = Map.of("input", inputText);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            // Log the call (but never log the actual input — PII policy)
            logger.info("Calling AI service: {} (input length: {} chars)", path, inputText.length());

            ResponseEntity<Map> response = restTemplate.postForEntity(
                aiServiceUrl + path, request, Map.class
            );

            logger.info("AI service responded: {} status={}", path, response.getStatusCode());
            return response.getBody();

        } catch (Exception e) {
            // Log the error but don't crash — AI being down should not break the app
            logger.error("AI service call to {} failed: {}", path, e.getMessage());
            return null;  // Caller must handle null gracefully
        }
    }

    /**
     * Convenience method — calls POST /api/generate-report
     */
    public Map<String, Object> generateReport(String input) {
        return callAiEndpoint("/api/generate-report", input);
    }

    /**
     * Convenience method — calls POST /api/describe
     */
    public Map<String, Object> describe(String input) {
        return callAiEndpoint("/api/describe", input);
    }

    /**
     * Convenience method — calls POST /api/recommend
     */
    public Map<String, Object> recommend(String input) {
        return callAiEndpoint("/api/recommend", input);
    }

    /**
     * Convenience method — calls POST /api/categorise
     */
    public Map<String, Object> categorise(String input) {
        return callAiEndpoint("/api/categorise", input);
    }

    /**
     * Convenience method — calls POST /api/analyse-document
     */
    public Map<String, Object> analyseDocument(String text) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // analyse-document uses "text" field instead of "input"
            Map<String, String> body = Map.of("text", text);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            logger.info("Calling AI service: /api/analyse-document (text length: {} chars)", text.length());

            ResponseEntity<Map> response = restTemplate.postForEntity(
                aiServiceUrl + "/api/analyse-document", request, Map.class
            );

            return response.getBody();

        } catch (Exception e) {
            logger.error("AI service call to /api/analyse-document failed: {}", e.getMessage());
            return null;
        }
    }
}