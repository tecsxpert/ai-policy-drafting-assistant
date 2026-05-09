package com.internship.tool.scheduler;

import com.internship.tool.entity.Policy;
import com.internship.tool.service.PolicyService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PolicyScheduler {

    private final PolicyService policyService;

    public PolicyScheduler(PolicyService policyService) {
        this.policyService = policyService;
    }

    // ================= OVERDUE CHECK =================
    @Scheduled(fixedRate = 60000)
    public void checkOverdue() {
        try {
            System.out.println("=== Overdue Scheduler Running ===");

            List<Policy> overduePolicies = policyService.getOverduePolicies();

            System.out.println("Overdue Count: " + overduePolicies.size());

            for (Policy p : overduePolicies) {
                System.out.println("OVERDUE: " + p.getTitle());
            }

        } catch (Exception e) {
            System.err.println("Error in Overdue Scheduler: " + e.getMessage());
        }
    }

    // ================= UPCOMING CHECK =================
    @Scheduled(fixedRate = 65000)
    public void checkUpcoming() {
        try {
            System.out.println("=== Upcoming Scheduler Running ===");

            List<Policy> upcomingPolicies = policyService.getUpcomingPolicies();

            System.out.println("Upcoming Count: " + upcomingPolicies.size());

            for (Policy p : upcomingPolicies) {
                System.out.println("UPCOMING: " + p.getTitle());
            }

        } catch (Exception e) {
            System.err.println("Error in Upcoming Scheduler: " + e.getMessage());
        }
    }

    // ================= WEEKLY SUMMARY =================
    @Scheduled(fixedRate = 70000)
    public void weeklySummary() {
        try {
            System.out.println("=== Weekly Summary Scheduler Running ===");

            long total = policyService.getDashboardStats()
                    .get("totalPolicies") instanceof Long l ? l : 0;

            System.out.println("Total Policies: " + total);

        } catch (Exception e) {
            System.err.println("Error in Weekly Summary: " + e.getMessage());
        }
    }
}