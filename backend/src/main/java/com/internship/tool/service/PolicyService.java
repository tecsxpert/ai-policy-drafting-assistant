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

import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service // Marks this as a Spring Service (business logic layer)
public class PolicyService {

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AiServiceClient aiServiceClient;

    // =========================================================
    // ✅ CREATE POLICY
    // - Saves policy in DB
    // - Sends email notification
    // - Triggers async AI processing
    // - Clears cache
    // =========================================================
    @CacheEvict(value = {"policies", "policy"}, allEntries = true)
    public Policy createPolicy(Policy policy) {

        // Save policy to database
        Policy savedPolicy = policyRepository.save(policy);

        // Send email notification after creation
        emailService.sendPolicyCreatedEmail(
                "test@gmail.com", // Replace with dynamic email later
                savedPolicy.getTitle()
        );

        // Trigger asynchronous AI report generation
        generateAiAsync(savedPolicy.getId(), savedPolicy.getDescription());

        return savedPolicy;
    }

    // =========================================================
    // ✅ GET ALL POLICIES (WITH PAGINATION + CACHING)
    // =========================================================
    @Cacheable(value = "policies")
    public Page<Policy> getAllPolicies(Pageable pageable) {
        System.out.println("Fetching policies from DB...");
        return policyRepository.findAll(pageable);
    }

    // =========================================================
    // ✅ GET POLICY BY ID (WITH CACHING)
    // Throws exception if not found
    // =========================================================
    @Cacheable(value = "policy", key = "#id")
    public Policy getPolicyById(Long id) {
        System.out.println("Fetching policy from DB...");
        return policyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Policy not found with id: " + id));
    }

    // =========================================================
    // ✅ DELETE POLICY
    // Clears cache after deletion
    // =========================================================
    @CacheEvict(value = {"policies", "policy"}, allEntries = true)
    public void deletePolicy(Long id) {

        // Ensure policy exists before deleting
        Policy policy = getPolicyById(id);

        policyRepository.delete(policy);
    }

    // =========================================================
    // ✅ CHECK OVERDUE POLICIES
    // - Checks if dueDate is passed
    // - Sends notification email
    // =========================================================
    public void checkOverduePolicies() {

        List<Policy> policies = policyRepository.findAll();

        for (Policy policy : policies) {

            // Check if due date exists and is before current time
            if (policy.getDueDate() != null &&
                policy.getDueDate().isBefore(LocalDateTime.now())) {

                // Send overdue notification email
                emailService.sendPolicyCreatedEmail(
                        "test@gmail.com", // Replace later
                        policy.getTitle() + " is overdue"
                );
            }
        }
    }

    // =========================================================
    // ✅ ASYNC AI REPORT GENERATION
    // - Runs in background (non-blocking)
    // - Calls AI service
    // - Stores AI result in DB
    // =========================================================
    @Async
    public void generateAiAsync(Long policyId, String input) {
        try {

            // Call AI service and get response
            Map<String, Object> response = aiServiceClient.generateReport(input);

            // Check if response is null or missing expected key
            if (response == null || !response.containsKey("data")) {
                System.out.println("AI failed or returned null");
                return;
            }

            // Extract AI result
            Object data = response.get("data");

            if (data == null) {
                System.out.println("AI data missing");
                return;
            }

            String aiResult = data.toString();

            // Fetch policy from DB
            Policy policy = policyRepository.findById(policyId).orElse(null);

            if (policy != null) {

                // Save AI-generated result into policy
                policy.setAiReport(aiResult);

                policyRepository.save(policy);
            }

        } catch (Exception e) {

            // Handle async errors safely
            System.out.println("Async error: " + e.getMessage());
        }
    }
}