package com.internship.tool.controller;

import com.internship.tool.entity.Policy;
import com.internship.tool.service.PolicyService;
import org.springframework.http.ResponseEntity;
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

    // ==================================================
    // CREATE POLICY
    // ==================================================
    @PostMapping("/create")
    public ResponseEntity<Policy> createPolicy(@RequestBody Policy policy) {
        return ResponseEntity.ok(policyService.createPolicy(policy));
    }

    // ==================================================
    // LIST POLICIES
    // ==================================================
    @GetMapping("/list")
    public ResponseEntity<List<Policy>> listPolicies() {
        return ResponseEntity.ok(policyService.getAllPoliciesList());
    }

    // ==================================================
    // PAGINATION
    // ==================================================
    @GetMapping("/all")
    public ResponseEntity<?> getAllPolicies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return ResponseEntity.ok(
                policyService.getAllPolicies(page, size, sortBy, sortDir)
        );
    }

    // ==================================================
    // GET BY ID
    // ==================================================
    @GetMapping("/{id}")
    public ResponseEntity<Policy> getPolicyById(@PathVariable Long id) {
        return ResponseEntity.ok(policyService.getPolicyById(id));
    }

    // ==================================================
    // UPDATE
    // ==================================================
    @PutMapping("/update/{id}")
    public ResponseEntity<Policy> updatePolicy(
            @PathVariable Long id,
            @RequestBody Policy policy
    ) {
        return ResponseEntity.ok(
                policyService.updatePolicy(id, policy)
        );
    }

    // ==================================================
    // DELETE (SOFT DELETE)
    // ==================================================
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePolicy(@PathVariable Long id) {
        policyService.deletePolicy(id);
        return ResponseEntity.ok("Policy deleted successfully");
    }

    // ==================================================
    // SEARCH
    // ==================================================
    @GetMapping("/search")
    public ResponseEntity<List<Policy>> search(@RequestParam String q) {
        return ResponseEntity.ok(policyService.searchPolicies(q));
    }

    // ==================================================
    // DASHBOARD STATS
    // ==================================================
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        return ResponseEntity.ok(policyService.getDashboardStats());
    }

    // ==================================================
    // 🔥 N+1 FIX TEST ENDPOINT
    // ==================================================
    @GetMapping("/optimized")
    public ResponseEntity<List<Policy>> getOptimizedPolicies() {
        return ResponseEntity.ok(policyService.getOptimizedPolicies());
    }
}