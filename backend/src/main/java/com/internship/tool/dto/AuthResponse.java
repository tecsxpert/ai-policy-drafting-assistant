package com.internship.tool.dto;

public class AuthResponse {

    private String token;

    // ✅ THIS IS THE IMPORTANT CONSTRUCTOR
    public AuthResponse(String token) {
        this.token = token;
    }

    // getter
    public String getToken() {
        return token;
    }

    // setter (optional but good practice)
    public void setToken(String token) {
        this.token = token;
    }
}