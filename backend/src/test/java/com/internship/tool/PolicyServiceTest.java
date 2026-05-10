package com.internship.tool;

import com.internship.tool.entity.Policy;
import com.internship.tool.repository.PolicyRepository;
import com.internship.tool.service.AiServiceClient;
import com.internship.tool.service.EmailService;
import com.internship.tool.service.PolicyService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private AiServiceClient aiServiceClient;

    @InjectMocks
    private PolicyService policyService;

    // Test 1: Create Policy (Happy Path)
    @Test
    void testCreatePolicy() {
        Policy policy = new Policy();
        policy.setTitle("Test Policy");

        when(policyRepository.save(policy)).thenReturn(policy);

        Policy result = policyService.createPolicy(policy);

        assertNotNull(result);
        assertEquals("Test Policy", result.getTitle());
    }

    // Test 2: Create Policy (Error Case)
    @Test
    void testCreatePolicy_Exception() {
        Policy policy = new Policy();

        when(policyRepository.save(policy))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> {
            policyService.createPolicy(policy);
        });
    }

    // Test 3: Get Policy By ID (Success)
    @Test
    void testGetPolicyById_Success() {
        Policy policy = new Policy();
        policy.setId(1L);

        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        Policy result = policyService.getPolicyById(1L);

        assertEquals(1L, result.getId());
    }

    // Test 4: Get Policy By ID (Not Found)
    @Test
    void testGetPolicyById_NotFound() {
        when(policyRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            policyService.getPolicyById(1L);
        });

        assertTrue(exception.getMessage().contains("Policy not found"));
    }

    // Test 5: Delete Policy (Success)
    @Test
    void testDeletePolicy_Success() {
        Policy policy = new Policy();
        policy.setId(1L);

        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        policyService.deletePolicy(1L);

        verify(policyRepository, times(1)).delete(policy);
    }

    // Test 6: Delete Policy (Not Found)
    @Test
    void testDeletePolicy_NotFound() {
        when(policyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            policyService.deletePolicy(1L);
        });
    }

    // Test 7: Get All Policies (Pagination)
    @Test
    void testGetAllPolicies() {
        Pageable pageable = mock(Pageable.class);
        
        @SuppressWarnings("unchecked")
        Page<Policy> page = (Page<Policy>) mock(Page.class);

        when(policyRepository.findAll(pageable)).thenReturn(page);

        Page<Policy> result = policyService.getAllPolicies(pageable);

        assertNotNull(result);
        verify(policyRepository, times(1)).findAll(pageable);
    }

    // Test 8: Create Policy (Null Safety)
    @Test
    void testCreatePolicy_NullCheck() {
        Policy policy = new Policy();

        when(policyRepository.save(policy)).thenReturn(policy);

        Policy result = policyService.createPolicy(policy);

        assertNotNull(result);
    }

    // Test 9: Get Policy By ID (Verify Call)
    @Test
    void testGetPolicyById_VerifyCall() {
        Policy policy = new Policy();
        policy.setId(1L);

        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        policyService.getPolicyById(1L);

        verify(policyRepository, times(1)).findById(1L);
    }

    // Test 10: Delete Policy (Verify Find Call)
    @Test
    void testDeletePolicy_VerifyFindCall() {
        Policy policy = new Policy();
        policy.setId(1L);

        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        policyService.deletePolicy(1L);

        verify(policyRepository).findById(1L);
    }

    // Test 11: Create Policy (Verify Save Call)
    @Test
    void testCreatePolicy_VerifySave() {
        Policy policy = new Policy();

        when(policyRepository.save(policy)).thenReturn(policy);

        policyService.createPolicy(policy);

        verify(policyRepository, times(1)).save(policy);
    }
}