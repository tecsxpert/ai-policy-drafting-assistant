package com.internship.tool.repository;

import com.internship.tool.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    @Query("""
        SELECT p FROM Policy p
        WHERE p.deleted = false
        AND (
            LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(p.description) LIKE LOWER(CONCAT('%', :q, '%'))
        )
    """)
    List<Policy> searchPolicies(@Param("q") String q);
}