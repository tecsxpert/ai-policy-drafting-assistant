package com.internship.tool.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.tool.entity.Policy;
import com.internship.tool.service.PolicyService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PolicyController.class)
class PolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PolicyService policyService;

    @Autowired
    private ObjectMapper objectMapper;

    // Test 1: Get Policy By ID
    @Test
    void testGetPolicyById() throws Exception {
        Policy policy = new Policy();
        policy.setId(1L);
        policy.setTitle("Test Policy");

        when(policyService.getPolicyById(1L)).thenReturn(policy);

        mockMvc.perform(get("/policies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Policy"));
    }

    // Test 2: Policy Not Found
    @Test
    void testGetPolicyById_NotFound() throws Exception {

        when(policyService.getPolicyById(1L))
                .thenThrow(new RuntimeException("Policy not found"));

        mockMvc.perform(get("/policies/1"))
                .andExpect(status().isNotFound());
    }
}