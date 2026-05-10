package com.internship.tool;

import com.internship.tool.config.JwtUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil = new JwtUtil();

    // Test 1: Generate Token
    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken("testuser");

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    // Test 2: Validate Token
    @Test
    void testValidateToken() {
        String token = jwtUtil.generateToken("testuser");

        boolean isValid = jwtUtil.validateToken(token, "testuser");

        assertTrue(isValid);
    }

    // Test 3: Invalid Token
    @Test
    void testInvalidToken() {
        assertThrows(Exception.class, () -> {
        jwtUtil.validateToken("invalid_token", "testuser");
      });
    }

    // Test 4: Extract Username
    @Test
    void testExtractUsername() {
        String token = jwtUtil.generateToken("testuser");

        String username = jwtUtil.extractUsername(token);

        assertEquals("testuser", username);
    }
}