package com.internship.tool.service;

import com.internship.tool.entity.Policy;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.time.LocalDateTime;
import java.util.List;

@Service // Business logic layer
public class PolicyService {

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private EmailService emailService;

    // ✅ CREATE policy (clear cache + send email)
    @CacheEvict(value = {"policies", "policy"}, allEntries = true)
    public Policy createPolicy(Policy policy) {

        // Save policy to DB
        Policy savedPolicy = policyRepository.save(policy);

        // Send email after creation
        emailService.sendPolicyCreatedEmail(
                "test@gmail.com", // replace later
                savedPolicy.getTitle()
        );

        return savedPolicy;
    }

    // ✅ GET all policies (cached)
    @Cacheable(value = "policies")
    public Page<Policy> getAllPolicies(Pageable pageable) {
        System.out.println("Fetching from DB...");
        return policyRepository.findAll(pageable);
    }

    // ✅ GET policy by ID (cached)
    @Cacheable(value = "policy", key = "#id")
    public Policy getPolicyById(Long id) {
        System.out.println("Fetching from DB...");
        return policyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Policy not found with id: " + id));
    }

    // ✅ DELETE policy (clear cache)
    @CacheEvict(value = {"policies", "policy"}, allEntries = true)
    public void deletePolicy(Long id) {
        Policy policy = getPolicyById(id); // ensure exists
        policyRepository.delete(policy);
    }

    // ✅ NEW: Check overdue policies and send email
    public void checkOverduePolicies() {

        // Fetch all policies
        List<Policy> policies = policyRepository.findAll();

        for (Policy policy : policies) {

            // Check if due date exists AND is expired
            if (policy.getDueDate() != null &&
                policy.getDueDate().isBefore(LocalDateTime.now())) {

                // Send overdue email
                emailService.sendPolicyCreatedEmail(
                        "test@gmail.com", // replace later
                        policy.getTitle() + " is overdue"
                );
            }
        }
    }
}