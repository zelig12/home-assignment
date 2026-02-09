package com.project.bedemo.service;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginService {

    public static final String PASSWORD = "r2isthebest";

    private final Set<String> validTokens = ConcurrentHashMap.newKeySet();

    public Optional<String> login(String email, String password) {
        if (!isValidEmail(email) || !PASSWORD.equals(password)) {
            return Optional.empty();
        }
        String token = UUID.randomUUID().toString();
        validTokens.add(token);
        return Optional.of(token);
    }

    public boolean invalidateToken(String token) {
        return token != null && validTokens.remove(token);
    }

    public boolean isValidToken(String token) {
        return token != null && validTokens.contains(token);
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}
