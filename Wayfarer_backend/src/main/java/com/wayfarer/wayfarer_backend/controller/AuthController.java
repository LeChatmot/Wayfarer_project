package com.wayfarer.wayfarer_backend.controller;

import com.wayfarer.wayfarer_backend.dto.auth_dto.AuthResponse;
import com.wayfarer.wayfarer_backend.dto.auth_dto.LoginRequest;
import com.wayfarer.wayfarer_backend.dto.auth_dto.RegisterRequest;
import com.wayfarer.wayfarer_backend.dto.auth_dto.TokenValidationResponse;
import com.wayfarer.wayfarer_backend.service.auth_service.AuthService;
import com.wayfarer.wayfarer_backend.service.auth_service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    private static final String REFRESH_COOKIE_NAME = "refresh_token";

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.register(request);
        setRefreshTokenCookie(response, authResponse.getRefreshToken());
        return ResponseEntity.ok(stripRefreshToken(authResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        setRefreshTokenCookie(response, authResponse.getRefreshToken());
        return ResponseEntity.ok(stripRefreshToken(authResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token manquant");
        }

        AuthResponse authResponse = authService.refresh(refreshToken);
        setRefreshTokenCookie(response, authResponse.getRefreshToken());
        return ResponseEntity.ok(stripRefreshToken(authResponse));
    }

    @GetMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validate(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        boolean accessValid = authHeader != null
                && authHeader.startsWith("Bearer ")
                && jwtService.isTokenValid(authHeader.substring(7));

        if (accessValid) {
            return ResponseEntity.ok(new TokenValidationResponse(true, false));
        }

        String refreshToken = extractRefreshTokenFromCookie(request);
        boolean refreshable = refreshToken != null
                && authService.isRefreshTokenValid(refreshToken);

        return ResponseEntity.ok(new TokenValidationResponse(false, refreshable));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken != null) {
            authService.revokeRefreshToken(refreshToken);
        }
        clearRefreshTokenCookie(response);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/isAdmin")
    public Boolean isUserAdmin(){
        return this.authService.isAdmin();
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/api/auth");
        cookie.setMaxAge((int) (refreshTokenExpirationMs / 1000));
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (REFRESH_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private AuthResponse stripRefreshToken(AuthResponse authResponse) {
        return new AuthResponse(authResponse.getAccessToken(), null);
    }
}