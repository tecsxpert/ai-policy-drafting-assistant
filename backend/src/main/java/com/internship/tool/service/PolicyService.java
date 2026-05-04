package com.internship.tool.service;

import com.internship.tool.entity.Policy;
import com.internship.tool.repository.PolicyRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PolicyService {

    private final PolicyRepository policyRepository;

    public PolicyService(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    public Policy createPolicy(Policy policy) {
        policy.setDeleted(false);
        return policyRepository.save(policy);
    }

    public List<Policy> getAllPolicies() {
        return policyRepository.findAll()
                .stream()
                .filter(p -> !p.isDeleted())
                .collect(Collectors.toList());
    }

    public Policy getPolicyById(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        if (policy.isDeleted()) {
            throw new RuntimeException("Policy is deleted");
        }

        return policy;
    }

    public Policy updatePolicy(Long id, Policy updatedPolicy) {
        Policy existingPolicy = getPolicyById(id);

        existingPolicy.setTitle(updatedPolicy.getTitle());
        existingPolicy.setDescription(updatedPolicy.getDescription());
        existingPolicy.setCategory(updatedPolicy.getCategory());
        existingPolicy.setStatus(updatedPolicy.getStatus());

        return policyRepository.save(existingPolicy);
    }

    public void softDeletePolicy(Long id) {
        Policy policy = getPolicyById(id);
        policy.setDeleted(true);
        policyRepository.save(policy);
    }

    public List<Policy> searchPolicies(String q) {
        return policyRepository.searchPolicies(q);
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        long total = policyRepository.count();
        long active = policyRepository.findAll()
                .stream()
                .filter(p -> !p.isDeleted())
                .count();

        stats.put("totalPolicies", total);
        stats.put("activePolicies", active);
        stats.put("deletedPolicies", total - active);

        return stats;
    }
}