package com.internship.tool.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuditServiceTest {

    private AuditService auditService = new AuditService();

    @Test
    void testAuditLog() {
        assertDoesNotThrow(() -> auditService.logAction("test"));
    }
}