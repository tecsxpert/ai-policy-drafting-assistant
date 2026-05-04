package com.internship.tool.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component // Spring will detect this class
public class PolicyScheduler {

    @Autowired
    private PolicyService policyService;

    // Runs every 1 minute
    @Scheduled(fixedRate = 60000)
    public void checkOverdue() {
        policyService.checkOverduePolicies();
    }
}