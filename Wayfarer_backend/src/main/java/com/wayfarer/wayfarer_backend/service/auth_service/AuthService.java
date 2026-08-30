package com.wayfarer.wayfarer_backend.service.auth_service;

import com.wayfarer.wayfarer_backend.dto.auth_dto.AuthResponse;
import com.wayfarer.wayfarer_backend.dto.auth_dto.LoginRequest;
import com.wayfarer.wayfarer_backend.dto.auth_dto.RegisterRequest;
import com.wayfarer.wayfarer_backend.model.RefreshToken;
import com.wayfarer.wayfarer_backend.model.Role;
import com.wayfarer.wayfarer_backend.model.User;
import com.wayfarer.wayfarer_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final long refreshTokenExpirationMs;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            @Value("${jwt.refresh-token-expiration-ms}") long refreshTokenExpirationMs) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email déjà utilisé");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nom d'utilisateur déjà utilisé");
        }
        User user = new User();
        user.setEmail(request.email());
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
        }
        return buildAuthResponse(user);
    }

    public AuthResponse refresh(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenValue)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token invalide"));

        if (refreshToken.isRevoked() || refreshTokenService.isExpired(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expiré ou révoqué");
        }

        User user = refreshToken.getUser();
        String newAccessToken = jwtService.generateAccessToken(user.getEmail());

        return new AuthResponse(newAccessToken, refreshTokenValue);
    }

    public void logout(String refreshTokenValue) {
        refreshTokenService.findByToken(refreshTokenValue)
                .ifPresent(token -> refreshTokenService.revokeAllForUser(token.getUser().getId()));
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, refreshTokenExpirationMs);
        return new AuthResponse(accessToken, refreshToken.getToken());
    }
}