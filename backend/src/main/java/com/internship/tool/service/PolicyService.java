package com.internship.tool.service;

import com.internship.tool.entity.Policy;
import com.internship.tool.repository.PolicyRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PolicyService {

    private final PolicyRepository policyRepository;

    public PolicyService(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    // ==================================================
    // CREATE POLICY
    // ==================================================
    public Policy createPolicy(Policy policy) {
        return policyRepository.save(policy);
    }

    // ==================================================
    // GET BY ID
    // ==================================================
    public Policy getPolicyById(Long id) {
        return policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
    }

    // ==================================================
    // SIMPLE LIST
    // ==================================================
    public List<Policy> getAllPoliciesList() {
        return policyRepository.findAll();
    }

    // ==================================================
    // UPDATE POLICY
    // ==================================================
    public Policy updatePolicy(Long id, Policy updated) {

        Policy existing = getPolicyById(id);

        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setCategory(updated.getCategory());
        existing.setStatus(updated.getStatus());
        existing.setDueDate(updated.getDueDate());

        return policyRepository.save(existing);
    }

    // ==================================================
    // SOFT DELETE
    // ==================================================
    public void deletePolicy(Long id) {

        Policy policy = getPolicyById(id);
        policy.setDeleted(true);

        policyRepository.save(policy);
    }

    // ==================================================
    // PAGINATION (FIXED - IMPORTANT)
    // ==================================================
    public Page<Policy> getAllPolicies(int page, int size, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        // 🔥 FIX: use deleted filter query (NOT findAll)
        return policyRepository.findByDeletedFalse(pageable);
    }

    // ==================================================
    // SEARCH
    // ==================================================
    public List<Policy> searchPolicies(String q) {
        return policyRepository.searchPolicies(q);
    }

    // ==================================================
    // DASHBOARD STATS (OPTIMIZED)
    // ==================================================
    public Map<String, Object> getDashboardStats() {

        Map<String, Object> stats = new HashMap<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusDays(7);

        long totalPolicies = policyRepository.count();
        long overduePolicies = policyRepository.countOverdue(now);
        long upcomingPolicies = policyRepository.countUpcoming(now, end);

        stats.put("totalPolicies", totalPolicies);
        stats.put("overduePolicies", overduePolicies);
        stats.put("upcomingPolicies", upcomingPolicies);

        stats.put("complianceScore",
                totalPolicies == 0
                        ? 100
                        : Math.max(0, 100 - (overduePolicies * 10))
        );

        return stats;
    }

    // ==================================================
    // SCHEDULER CHECK
    // ==================================================
    public void checkOverduePolicies() {

        long overdueCount = policyRepository.countOverdue(LocalDateTime.now());

        System.out.println("Overdue Policies: " + overdueCount);
    }

    // ==================================================
    // OVERDUE LIST
    // ==================================================
    public List<Policy> getOverduePolicies() {
        return policyRepository.findByDueDateBeforeAndDeletedFalse(LocalDateTime.now());
    }

    // ==================================================
    // UPCOMING LIST
    // ==================================================
    public List<Policy> getUpcomingPolicies() {
        return policyRepository.findByDueDateBetweenAndDeletedFalse(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7)
        );
    }

    // ==================================================
    // OPTIMIZED FETCH (JOIN FETCH)
    // ==================================================
    public List<Policy> getOptimizedPolicies() {
        return policyRepository.findPoliciesWithLogs();
    }
}