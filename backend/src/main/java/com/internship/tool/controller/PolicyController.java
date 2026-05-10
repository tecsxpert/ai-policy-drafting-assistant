package com.internship.tool.controller;

import com.internship.tool.entity.Policy;
import com.internship.tool.service.PolicyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    @Autowired
    private PolicyService policyService;

    // GET ALL
    @Operation(
        summary = "Get All Policies",
        description = "Fetch all policies with pagination support"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Policies fetched successfully")
    })
    @GetMapping("/all")
    public ResponseEntity<Page<Policy>> getAllPolicies(Pageable pageable) {
        Page<Policy> policies = policyService.getAllPolicies(pageable);
        return ResponseEntity.ok(policies);
    }

    // GET BY ID
    @Operation(
        summary = "Get Policy by ID",
        description = "Fetch a policy using its unique ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Policy found"),
        @ApiResponse(responseCode = "404", description = "Policy not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Policy> getPolicyById(@PathVariable Long id) {
        Policy policy = policyService.getPolicyById(id);
        return ResponseEntity.ok(policy);
    }

    // CREATE
    @Operation(
        summary = "Create Policy",
        description = "Create a new policy with validation"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Policy created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/create")
    public ResponseEntity<Policy> createPolicy(@Valid @RequestBody Policy policy) {
        Policy savedPolicy = policyService.createPolicy(policy);
        return new ResponseEntity<>(savedPolicy, HttpStatus.CREATED);
    }

    // DELETE
    @Operation(
        summary = "Delete Policy",
        description = "Delete a policy by its ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Policy deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Policy not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePolicy(@PathVariable Long id) {
        policyService.deletePolicy(id);
        return ResponseEntity.ok("Policy deleted successfully");
    }
}