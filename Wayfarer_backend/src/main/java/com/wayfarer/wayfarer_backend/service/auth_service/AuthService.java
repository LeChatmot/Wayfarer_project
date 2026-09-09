package com.wayfarer.wayfarer_backend.service.auth_service;

import com.wayfarer.wayfarer_backend.dto.auth_dto.AuthResponse;
import com.wayfarer.wayfarer_backend.dto.auth_dto.LoginRequest;
import com.wayfarer.wayfarer_backend.dto.auth_dto.RegisterRequest;
import com.wayfarer.wayfarer_backend.model.RefreshToken;
import com.wayfarer.wayfarer_backend.model.Role;
import com.wayfarer.wayfarer_backend.model.User;
import com.wayfarer.wayfarer_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final CurrentUserService currentUserService;
    private final EmailCryptoService emailCryptoService;
    private final long refreshTokenExpirationMs;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService, CurrentUserService currentUserService,
            EmailCryptoService emailCryptoService,
            @Value("${jwt.refresh-token-expiration-ms}") long refreshTokenExpirationMs) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.currentUserService = currentUserService;
        this.emailCryptoService = emailCryptoService;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    public AuthResponse register(RegisterRequest request) {
        String emailHash = emailCryptoService.computeHash(request.email());

        if (userRepository.existsByEmailHash(emailHash)) {
            this.logger.warn("User with this email already exists");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email déjà utilisé");
        }
        if (userRepository.existsByUsername(request.username())) {
            this.logger.warn("Username {} already exists", request.username());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nom d'utilisateur déjà utilisé");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setEmailHash(emailHash);
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);

        this.logger.info("create user {}", user.getId());
        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        String emailHash = emailCryptoService.computeHash(request.email());

        User user = userRepository.findByEmailHash(emailHash)
                .orElseThrow(() -> {
                    this.logger.info("User not found");
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
                });
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            this.logger.warn("Wrong password for user {}", user.getId());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
        }
        return buildAuthResponse(user);
    }

    public AuthResponse refresh(String refreshTokenValue) {
        RefreshToken oldToken = refreshTokenService.findByToken(refreshTokenValue)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token invalide"));

        if (oldToken.isRevoked() || oldToken.isExpired()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expiré ou révoqué");
        }

        User user = oldToken.getUser();

        refreshTokenService.revoke(oldToken);

        String newAccessToken = jwtService.generateAccessToken(user.getId());
        RefreshToken newRefreshToken = refreshTokenService.createFor(user);

        return new AuthResponse(newAccessToken, newRefreshToken.getToken());
    }

    public boolean isRefreshTokenValid(String refreshTokenValue) {
        return refreshTokenService.findByToken(refreshTokenValue)
                .map(token -> !token.isRevoked() && !token.isExpired())
                .orElse(false);
    }

    public void logout(String refreshTokenValue) {
        refreshTokenService.findByToken(refreshTokenValue)
                .ifPresent(token -> refreshTokenService.revokeAllForUser(token.getUser().getId()));
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user.getId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, refreshTokenExpirationMs);
        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    public void revokeRefreshToken(String refreshTokenValue) {
        refreshTokenService.findByToken(refreshTokenValue)
                .ifPresent(refreshTokenService::revoke);
    }

    public boolean isAdmin(){
        return this.currentUserService.isAdmin();
    }
}