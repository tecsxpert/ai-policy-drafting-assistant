package com.internship.tool.service;

import org.springframework.stereotype.Service;

@Service
public class AuditService {

    public void logAction(String action) {
        System.out.println("Audit: " + action);
    }
}