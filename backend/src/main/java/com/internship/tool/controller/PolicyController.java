package com.internship.tool.controller;

import com.internship.tool.entity.Policy;
import com.internship.tool.service.PolicyService;
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

    // GET /all (Paginated)
    @GetMapping("/all")
    public ResponseEntity<Page<Policy>> getAllPolicies(Pageable pageable) {
        Page<Policy> policies = policyService.getAllPolicies(pageable);
        return ResponseEntity.ok(policies);
    }

    // GET /{id} with 404 handling
    @GetMapping("/{id}")
    public ResponseEntity<Policy> getPolicyById(@PathVariable Long id) {
        Policy policy = policyService.getPolicyById(id);
        return ResponseEntity.ok(policy);
    }

    // POST /create with validation
    @PostMapping("/create")
    public ResponseEntity<Policy> createPolicy(@Valid @RequestBody Policy policy) {
        Policy savedPolicy = policyService.createPolicy(policy);
        return new ResponseEntity<>(savedPolicy, HttpStatus.CREATED);
    }

    // DELETE 
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePolicy(@PathVariable Long id) {
        policyService.deletePolicy(id);
        return ResponseEntity.ok("Policy deleted successfully");
    }
}