package com.internship.tool.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.tool.config.JwtAuthFilter;
import com.internship.tool.config.JwtUtil;
import com.internship.tool.entity.Policy;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.service.PolicyService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false) // Disable security filters for testing
@WebMvcTest(PolicyController.class) // Load only controller layer
class PolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PolicyService policyService; // Mock service layer

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtil jwtUtil; // Mock JWT utility

    @MockBean
    private JwtAuthFilter jwtAuthFilter; // Mock JWT filter

    // =========================================================
    // Test 1: Get Policy By ID (Success Case)
    // =========================================================
    @Test
    void testGetPolicyById() throws Exception {

        // Create mock policy
        Policy policy = new Policy();
        policy.setId(1L);
        policy.setTitle("Test Policy");
        policy.setDescription("Test Description");
        policy.setCategory("IT");
        policy.setStatus("ACTIVE");

        // Mock service response
        when(policyService.getPolicyById(1L)).thenReturn(policy);

        // Perform GET request and validate response
        mockMvc.perform(get("/api/policies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Policy"));
    }

    // =========================================================
    // Test 2: Get Policy By ID (Not Found Case)
    // =========================================================
    @Test
    void testGetPolicyById_NotFound() throws Exception {

        // Mock service to throw exception
        when(policyService.getPolicyById(1L))
                .thenThrow(new ResourceNotFoundException("Policy not found"));

        // Perform GET request and expect 404
        mockMvc.perform(get("/api/policies/1"))
                .andExpect(status().isNotFound());
    }
}