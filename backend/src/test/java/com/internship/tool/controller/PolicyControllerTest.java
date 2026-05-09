package com.internship.tool.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.tool.entity.Policy;
import com.internship.tool.service.PolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PolicyController.class)
@AutoConfigureMockMvc(addFilters = false)
class PolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    
    @MockBean
    private PolicyService policyService;

    @Autowired
    private ObjectMapper objectMapper;

    private Policy policy1;

    @BeforeEach
    void setUp() {
        objectMapper.findAndRegisterModules();

        policy1 = new Policy();
        policy1.setId(1L);
        policy1.setTitle("Test Policy");
        policy1.setDescription("Desc");
        policy1.setCategory("IT");
        policy1.setStatus("ACTIVE");
        policy1.setCreatedBy("admin");
        policy1.setCreatedAt(LocalDateTime.now());
        policy1.setUpdatedAt(LocalDateTime.now());
        policy1.setDeleted(false);
        policy1.setDueDate(LocalDateTime.now().plusDays(10));
    }

    @Test
    void testCreatePolicy() throws Exception {

        when(policyService.createPolicy(any(Policy.class)))
                .thenReturn(policy1);

        mockMvc.perform(post("/api/policies/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(policy1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetAllPolicies() throws Exception {

        when(policyService.getAllPolicies(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(org.springframework.data.domain.Page.empty());

        mockMvc.perform(get("/api/policies/all"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeletePolicy() throws Exception {

        doNothing().when(policyService).deletePolicy(1L);

        mockMvc.perform(delete("/api/policies/1"))
                .andExpect(status().isOk());
    }
}