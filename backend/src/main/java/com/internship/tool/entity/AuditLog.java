package com.internship.tool.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "action")
    private String action;

    @Column(columnDefinition = "TEXT")
    private String oldData;

    @Column(columnDefinition = "TEXT")
    private String newData;

    @Column(name = "changed_by")
    private String changedBy;

    @Column(name = "changed_at")
    private LocalDateTime changedAt;

    // 🔥 MANY logs belong to ONE policy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;
}