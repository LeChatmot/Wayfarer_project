package com.wayfarer.wayfarer_backend.controller;

import com.wayfarer.wayfarer_backend.dto.auth_dto.*;
import com.wayfarer.wayfarer_backend.service.auth_service.AuthService;
import com.wayfarer.wayfarer_backend.service.auth_service.AuthValidationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthValidationService authValidationService;

    public AuthController(AuthService authService, AuthValidationService authValidationService) {
        this.authService = authService;
        this.authValidationService = authValidationService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request.getRefreshToken());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
    }

    @PostMapping("/validate")
    public TokenValidationResponse validate(@RequestHeader("Authorization") String authHeader) {
        String accessToken = extractBearerToken(authHeader);
        return authValidationService.validateTokens(accessToken);
    }

    private String extractBearerToken(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}