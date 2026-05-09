package com.internship.tool.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.tool.entity.AuditLog;

import com.internship.tool.service.AuditService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Aspect
public class AuditAspect {

    private final AuditService auditService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuditAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    @Around("execution(* com.internship.tool.service.PolicyService.*(..))")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {

        String method = joinPoint.getSignature().getName();

        System.out.println("🔥 AOP ENTERED METHOD -> " + method);

        String action;

        if (method.startsWith("create")) action = "CREATE";
        else if (method.startsWith("update")) action = "UPDATE";
        else if (method.startsWith("delete")) action = "DELETE";
        else return joinPoint.proceed();

        Object result = joinPoint.proceed();

        try {
            String username = "SYSTEM";

            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                username = SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName();
            }

            AuditLog log = new AuditLog();

            // ✅ FIXED FIELD NAME
            log.setEntityType("Policy");
            log.setAction(action);
            log.setChangedBy(username);
            log.setChangedAt(LocalDateTime.now());

            if (result != null) {
                log.setNewData(objectMapper.writeValueAsString(result));
            }

            auditService.save(log);

            System.out.println("✅ AUDIT SAVED SUCCESSFULLY");

        } catch (Exception e) {
            System.out.println("❌ AUDIT FAILED");
            e.printStackTrace();
        }

        return result;
    }
}