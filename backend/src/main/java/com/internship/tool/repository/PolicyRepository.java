package com.internship.tool.repository;

import com.internship.tool.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Marks this as a Spring repository (DAO layer)
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    // JpaRepository provides built-in CRUD operations (save, findAll, findById, delete, etc.)
}