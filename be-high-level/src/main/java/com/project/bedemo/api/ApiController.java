package com.project.bedemo.api;

import com.project.bedemo.service.GameService;
import com.project.bedemo.service.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final LoginService loginService;
    private final GameService gameService;

    public ApiController(LoginService loginService, GameService gameService) {
        this.loginService = loginService;
        this.gameService = gameService;
    }

    private static String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return authorizationHeader.substring(BEARER_PREFIX.length()).trim();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return loginService.login(request.getEmail(), request.getPassword())
                .map(token -> ResponseEntity.ok(Map.of("token", token)))
                .orElse(ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid email or password")));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = extractBearerToken(authorization);
        if (!loginService.invalidateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/try_luck")
    public ResponseEntity<?> tryLuck(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = extractBearerToken(authorization);
        if (!loginService.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }
        boolean win = gameService.tryLuck();
        return ResponseEntity.ok(Map.of("win", win));
    }
}
