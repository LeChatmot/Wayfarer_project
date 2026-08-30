package com.wayfarer.wayfarer_backend.service.auth_service;

import com.wayfarer.wayfarer_backend.dto.auth_dto.TokenValidationResponse;
import com.wayfarer.wayfarer_backend.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthValidationService {

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthValidationService(JwtService jwtService, RefreshTokenRepository refreshTokenRepository) {
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public TokenValidationResponse validateTokens(String accessToken) {
        boolean accessValid = accessToken != null && jwtService.isTokenValid(accessToken);

        if (accessValid) {
            return new TokenValidationResponse(true, false);
        }

        String userEmail = extractEmailSafely(accessToken);
        boolean refreshable = userEmail != null && hasValidRefreshToken(userEmail);

        return new TokenValidationResponse(false, refreshable);
    }

    private String extractEmailSafely(String token) {
        try {
            return jwtService.extractEmail(token);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean hasValidRefreshToken(String email) {
        return refreshTokenRepository.findByUserEmail(email)
                .isExpired();
    }
}