package com.wayfarer.wayfarer_backend.service.auth_service;

import com.wayfarer.wayfarer_backend.model.Role;
import com.wayfarer.wayfarer_backend.model.User;
import com.wayfarer.wayfarer_backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName().equals("anonymousUser")) {
            throw new IllegalStateException("Utilisateur non authentifié");
        }

        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() ->
                        new IllegalStateException("Utilisateur authentifié introuvable"));
    }

    public Optional<User> findCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return Optional.empty();
        }

        return userRepository.findByUsername(authentication.getName());
    }

    public boolean isAdmin(){
        Optional<User> user = this.findCurrentUser();
        return user.filter(value -> Role.ROLE_ADMIN.equals(value.getRole())).isPresent();
    }
}
