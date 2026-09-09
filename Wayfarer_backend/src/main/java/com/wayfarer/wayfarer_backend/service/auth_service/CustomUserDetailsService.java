package com.wayfarer.wayfarer_backend.service.auth_service;

import com.wayfarer.wayfarer_backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
    UserdetailsService implémente la méthode loadUserByUsername qu'on override ici, on utilise l'id utilisateur comme username
     */
    @Override
    public UserDetails loadUserByUsername(String userId) {
        int id;
        try {
            id = Integer.parseInt(userId);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Identifiant invalide");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));
    }
}