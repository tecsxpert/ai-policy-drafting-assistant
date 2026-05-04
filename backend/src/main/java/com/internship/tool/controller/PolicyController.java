package com.internship.tool.controller;

import com.internship.tool.entity.Policy;
import com.internship.tool.service.PolicyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PostMapping("/create")
    public ResponseEntity<Policy> createPolicy(@RequestBody Policy policy) {
        return ResponseEntity.ok(policyService.createPolicy(policy));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Policy>> getAllPolicies() {
        return ResponseEntity.ok(policyService.getAllPolicies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Policy> getPolicyById(@PathVariable Long id) {
        return ResponseEntity.ok(policyService.getPolicyById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<Policy> updatePolicy(
            @PathVariable Long id,
            @RequestBody Policy policyDetails) {
        return ResponseEntity.ok(policyService.updatePolicy(id, policyDetails));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePolicy(@PathVariable Long id) {
        policyService.softDeletePolicy(id);
        return ResponseEntity.ok("Policy soft deleted successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VIEWER')")
    @GetMapping("/search")
    public ResponseEntity<List<Policy>> searchPolicies(@RequestParam String q) {
        return ResponseEntity.ok(policyService.searchPolicies(q));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(policyService.getDashboardStats());
    }
}