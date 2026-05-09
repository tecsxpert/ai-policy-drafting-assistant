package com.internship.tool.service;

import com.internship.tool.entity.AuditLog;
import com.internship.tool.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public AuditLog save(AuditLog log) {

        try {
            System.out.println("📝 Saving audit log -> entityType: "
                    + log.getEntityType()
                    + ", action: "
                    + log.getAction()
                    + ", user: "
                    + log.getChangedBy());

            AuditLog saved = auditLogRepository.save(log);

            System.out.println("✅ Audit saved with ID: " + saved.getId());

            return saved;

        } catch (Exception e) {
            System.out.println("❌ Audit save failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}