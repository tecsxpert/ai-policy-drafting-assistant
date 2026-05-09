package com.internship.tool.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "policies",
    indexes = {
        @Index(name = "idx_policy_due_date", columnList = "due_date"),
        @Index(name = "idx_policy_deleted", columnList = "is_deleted"),
        @Index(name = "idx_policy_deleted_due", columnList = "is_deleted, due_date")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String category;

    @NotBlank
    private String status;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(name = "is_deleted")
    private boolean deleted = false;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    // 🔥 ONE POLICY → MANY AUDIT LOGS
    @Builder.Default
    @OneToMany(
        mappedBy = "policy",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<AuditLog> auditLogs = new ArrayList<>();
}