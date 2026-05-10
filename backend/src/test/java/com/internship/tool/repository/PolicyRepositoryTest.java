package com.internship.tool.repository;

import com.internship.tool.entity.Policy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PolicyRepositoryTest {

    @Autowired
    private PolicyRepository policyRepository;

    // Test 1: Save Policy
    @Test
    void testSavePolicy() {
        Policy policy = new Policy();
        policy.setTitle("Test Policy");
        policy.setDescription("Test Description");
        policy.setCategory("Security");
        policy.setStatus("ACTIVE");

        Policy saved = policyRepository.save(policy);

        assertNotNull(saved.getId());
        assertEquals("Test Policy", saved.getTitle());
    }

    // Test 2: Find Policy By ID
    @Test
    void testFindById() {
        Policy policy = new Policy();
        policy.setTitle("Find Test");
        policy.setDescription("Find Description");
        policy.setCategory("IT");
        policy.setStatus("ACTIVE");

        Policy saved = policyRepository.save(policy);

        Policy result = policyRepository.findById(saved.getId()).orElse(null);

        assertNotNull(result);
        assertEquals("Find Test", result.getTitle());
    }

    // Test 3: Delete Policy
    @Test
    void testDeletePolicy() {
        Policy policy = new Policy();
        policy.setTitle("Delete Test");
        policy.setDescription("Delete Description");
        policy.setCategory("Finance");
        policy.setStatus("ACTIVE");

        Policy saved = policyRepository.save(policy);

        policyRepository.deleteById(saved.getId());

        boolean exists = policyRepository.findById(saved.getId()).isPresent();

        assertFalse(exists);
    }

    // Test 4: Count Policies
    @Test
    void testCountPolicies() {
        Policy p1 = new Policy();
        p1.setTitle("P1");
        p1.setDescription("Desc1");
        p1.setCategory("HR");
        p1.setStatus("ACTIVE");

        Policy p2 = new Policy();
        p2.setTitle("P2");
        p2.setDescription("Desc2");
        p2.setCategory("IT");
        p2.setStatus("ACTIVE");

        policyRepository.save(p1);
        policyRepository.save(p2);

        long count = policyRepository.count();

        assertTrue(count >= 2);
    }
}