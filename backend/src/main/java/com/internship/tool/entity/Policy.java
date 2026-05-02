package com.internship.tool.entity;

import jakarta.validation.constraints.*; // Validation annotations
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity // Maps class to DB table
@Table(name = "policies") // Table name
@EntityListeners(AuditingEntityListener.class) // Enables auditing
@Data // Lombok: getters, setters
@NoArgsConstructor // No-args constructor
@AllArgsConstructor // All-args constructor
@Builder // Builder pattern
public class Policy {

    @Id // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto increment
    private Long id;

    @NotBlank(message = "Title is required") // Validation
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Description is required") // Validation
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Category is required") // Validation
    @Column
    private String category;

    @NotBlank(message = "Status is required") // Validation
    @Column
    private String status;

    @CreatedDate // Auto set creation time
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // Auto update time
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime dueDate; // used for overdue check
}