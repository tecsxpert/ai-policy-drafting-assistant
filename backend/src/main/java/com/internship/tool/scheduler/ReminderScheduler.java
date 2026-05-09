package com.internship.tool.scheduler;

import com.internship.tool.entity.Policy;
import com.internship.tool.repository.PolicyRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReminderScheduler {

    private final PolicyRepository policyRepository;

    public ReminderScheduler(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Scheduled(cron = "0 * * * * ?")
    public void dailyOverdueReminder() {

        System.out.println("=== Overdue Scheduler Running ===");

        List<Policy> overduePolicies =
                policyRepository.findByDueDateBeforeAndDeletedFalse(
                        LocalDateTime.now()
                );

        System.out.println("Overdue Policies Count: " + overduePolicies.size());

        for (Policy policy : overduePolicies) {
            System.out.println("Overdue Policy: " + policy.getTitle());
        }
    }

    @Scheduled(cron = "0 * * * * ?")
    public void upcomingdueDateReminder() {

        System.out.println("=== Upcoming Scheduler Running ===");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next7Days = now.plusDays(7);

        List<Policy> upcomingPolicies =
                policyRepository.findByDueDateBetweenAndDeletedFalse(
                        now,
                        next7Days
                );

        System.out.println("Upcoming 7-day Policies Count: " + upcomingPolicies.size());

        for (Policy policy : upcomingPolicies) {
            System.out.println("Upcoming Policy: " + policy.getTitle());
        }
    }

    @Scheduled(cron = "0 * * * * ?")
    public void weeklySummary() {

        System.out.println("=== Weekly Summary Scheduler Running ===");

        long totalPolicies = policyRepository.count();

        System.out.println("Total Policies: " + totalPolicies);
    }
}