package com.wayfarer.wayfarer_backend.repository;

import com.wayfarer.wayfarer_backend.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.user.id = :userId AND r.revoked = false")
    void revokeAllByUserId(Integer userId);

    @Query("SELECT r FROM RefreshToken r WHERE r.user.email = :email AND r.revoked = false ORDER BY r.expiryDate DESC")
    Optional<RefreshToken> findTopByUserEmail(String email);
}