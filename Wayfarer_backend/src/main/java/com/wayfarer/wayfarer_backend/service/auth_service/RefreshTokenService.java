package com.wayfarer.wayfarer_backend.service.auth_service;

import com.wayfarer.wayfarer_backend.model.RefreshToken;
import com.wayfarer.wayfarer_backend.model.User;
import com.wayfarer.wayfarer_backend.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }

    public RefreshToken createRefreshToken(User user, long expirationMs) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(jwtService.generateOpaqueRefreshToken());
        refreshToken.setExpiryDate(Instant.now().plusMillis(expirationMs));
        refreshToken.setRevoked(false);
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken createFor(User user) {
        return createRefreshToken(user, jwtService.getRefreshTokenExpirationMs());
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public boolean isExpired(RefreshToken refreshToken) {
        return refreshToken.isExpired();
    }

    public void revoke(RefreshToken refreshToken) {
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    public void revokeAllForUser(Integer userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }
}