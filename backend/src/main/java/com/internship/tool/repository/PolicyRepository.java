package com.internship.tool.repository;

import com.internship.tool.entity.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    // =========================
    // SEARCH
    // =========================
    @Query("""
        SELECT p FROM Policy p
        WHERE p.deleted = false
        AND (
            LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(p.description) LIKE LOWER(CONCAT('%', :q, '%'))
        )
    """)
    List<Policy> searchPolicies(@Param("q") String q);


    // =========================
    // PAGINATION (DAY 12 FIX)
    // =========================
    Page<Policy> findByDeletedFalse(Pageable pageable);


    // =========================
    // LIST (UI ONLY)
    // =========================
    List<Policy> findByDueDateBeforeAndDeletedFalse(LocalDateTime dateTime);

    List<Policy> findByDueDateBetweenAndDeletedFalse(
            LocalDateTime start,
            LocalDateTime end
    );


    // =========================
    // PERFORMANCE COUNTS
    // =========================
    @Query("""
        SELECT COUNT(p)
        FROM Policy p
        WHERE p.deleted = false
        AND p.dueDate < :now
    """)
    long countOverdue(@Param("now") LocalDateTime now);


    @Query("""
        SELECT COUNT(p)
        FROM Policy p
        WHERE p.deleted = false
        AND p.dueDate BETWEEN :start AND :end
    """)
    long countUpcoming(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


    // =========================
    // N+1 FIX (JOIN FETCH)
    // =========================
    @Query("""
        SELECT DISTINCT p
        FROM Policy p
        LEFT JOIN FETCH p.auditLogs
        WHERE p.deleted = false
    """)
    List<Policy> findPoliciesWithLogs();
}