package com.wayfarer.wayfarer_backend.service.user_service;

import com.wayfarer.wayfarer_backend.dto.user_dto.UserResponse;
import com.wayfarer.wayfarer_backend.dto.user_dto.UserUpdateRequest;
import com.wayfarer.wayfarer_backend.mapper.UserMapper;
import com.wayfarer.wayfarer_backend.model.User;
import com.wayfarer.wayfarer_backend.repository.UserRepository;
import com.wayfarer.wayfarer_backend.service.auth_service.CurrentUserService;
import com.wayfarer.wayfarer_backend.service.auth_service.EmailCryptoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final CurrentUserService currentUserService;
    private final EmailCryptoService emailCryptoService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(CurrentUserService currentUserService, EmailCryptoService emailCryptoService, UserRepository userRepository, UserMapper userMapper) {
        this.currentUserService = currentUserService;
        this.emailCryptoService = emailCryptoService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponse getCurrentUserProfile() {
        return userMapper.toResponse(currentUserService.getCurrentUser());
    }

    @Transactional
    public UserResponse updateCurrentUserProfile(UserUpdateRequest request) {
        User user = currentUserService.getCurrentUser();

        if (!user.getUsername().equals(request.username())
                && userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà utilisé");
        }

        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setEmailHash(this.emailCryptoService.computeHash(request.email()));

        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Transactional
    public void deleteUser(){
        this.userRepository.delete(this.currentUserService.getCurrentUser());
    }
}