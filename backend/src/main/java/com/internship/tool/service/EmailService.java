package com.internship.tool.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;

@Service // Marks this class as service component
public class EmailService {

    @Autowired(required = false) 
    // Injects JavaMailSender (optional to avoid crash if not configured)
    private JavaMailSender mailSender;

    @Autowired(required = false) 
    // Injects Thymeleaf template engine
    private TemplateEngine templateEngine;

    // Method to send HTML email
    public void sendPolicyCreatedEmail(String to, String title) {

        // If mail config is missing, skip sending
        if (mailSender == null || templateEngine == null) {
            System.out.println("Email service not configured. Skipping email.");
            return;
        }

        try {
            // Create context for Thymeleaf (data to pass)
            Context context = new Context();
            context.setVariable("title", title); // dynamic value

            // Process HTML template with data
            String htmlContent = templateEngine.process("policy-email", context);

            // Create MIME email (for HTML support)
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to); // Receiver email
            helper.setSubject("New Policy Created"); // Email subject
            helper.setText(htmlContent, true); // true = HTML content

            // Send email
            mailSender.send(message);

            System.out.println("HTML Email sent successfully!");

        } catch (Exception e) {
            e.printStackTrace(); // Print error if occurs
        }
    }
}