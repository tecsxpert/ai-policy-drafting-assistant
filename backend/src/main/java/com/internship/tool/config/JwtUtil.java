package com.internship.tool.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component // Makes this class Spring-managed
public class JwtUtil {

    private final String SECRET = "mysecretkeymysecretkeymysecretkey"; // use env later

    // Generate signing key
    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // Generate Token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // store username
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract username
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Validate token
    public boolean validateToken(String token, String username) {
        String extractedUsername = extractUsername(token);
        return extractedUsername.equals(username) && !isTokenExpired(token);
    }

    // Check expiration
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Extract all claims
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}