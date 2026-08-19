package com.wayfarer.wayfarer_backend.service.auth_service;

import com.wayfarer.wayfarer_backend.dto.auth_dto.AuthResponse;
import com.wayfarer.wayfarer_backend.dto.auth_dto.LoginRequest;
import com.wayfarer.wayfarer_backend.dto.auth_dto.RegisterRequest;
import com.wayfarer.wayfarer_backend.model.Role;
import com.wayfarer.wayfarer_backend.model.User;
import com.wayfarer.wayfarer_backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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
        return new AuthResponse(jwtService.generateToken(user.getEmail()), user.getUsername(), user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
        }
        return new AuthResponse(jwtService.generateToken(user.getEmail()), user.getUsername(), user.getEmail());
    }
}


