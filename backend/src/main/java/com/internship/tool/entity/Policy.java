package com.internship.tool.entity;

import jakarta.validation.constraints.*; // Validation annotations
import jakarta.persistence.*; // JPA annotations

import lombok.*; // Lombok annotations

import org.springframework.data.annotation.CreatedDate; // Auto creation timestamp
import org.springframework.data.annotation.LastModifiedDate; // Auto update timestamp
import org.springframework.data.jpa.domain.support.AuditingEntityListener; // Enables auditing

import io.swagger.v3.oas.annotations.media.Schema; // Swagger documentation

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)

@Entity // Marks class as database entity
@Table(name = "policies") // Database table name
@EntityListeners(AuditingEntityListener.class) // Enables automatic timestamps

@Data // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor // Default constructor
@AllArgsConstructor // All arguments constructor
@Builder // Builder pattern support

@Schema(description = "Policy entity representing policy details")
public class Policy {

    // =========================================================
    // Primary Key
    // =========================================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Schema(
        description = "Unique ID of the policy",
        example = "1"
    )
    private Long id;

    // =========================================================
    // Policy Title
    // =========================================================
    @NotBlank(message = "Title is required")

    @Column(nullable = false)

    @Schema(
        description = "Title of the policy",
        example = "Data Security Policy"
    )
    private String title;

    // =========================================================
    // Policy Description
    // =========================================================
    @NotBlank(message = "Description is required")

    @Column(columnDefinition = "TEXT")

    @Schema(
        description = "Detailed description of the policy",
        example = "This policy ensures data protection and encryption."
    )
    private String description;

    // =========================================================
    // Policy Category
    // =========================================================
    @NotBlank(message = "Category is required")

    @Column

    @Schema(
        description = "Category of the policy",
        example = "Security"
    )
    private String category;

    // =========================================================
    // Policy Status
    // =========================================================
    @NotBlank(message = "Status is required")

    @Column

    @Schema(
        description = "Current status of the policy",
        example = "ACTIVE"
    )
    private String status;

    // =========================================================
    // Created Timestamp
    // =========================================================
    @CreatedDate // Automatically set during insert

    @Column(updatable = false)

    @Schema(
        description = "Policy creation timestamp"
    )
    private LocalDateTime createdAt;

    // =========================================================
    // Updated Timestamp
    // =========================================================
    @LastModifiedDate // Automatically updated during modification

    @Schema(
        description = "Policy updated timestamp"
    )
    private LocalDateTime updatedAt;

    // =========================================================
    // Due Date
    // =========================================================
    @Column

    @Schema(
        description = "Due date for policy review"
    )
    private LocalDateTime dueDate;

    // =========================================================
    // AI Generated Report
    // =========================================================
    @Column(columnDefinition = "TEXT")

    @Schema(
        description = "AI generated report for the policy",
        example = "This policy complies with security standards and requires periodic review."
    )
    private String aiReport;
}