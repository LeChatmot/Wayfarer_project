package com.wayfarer.wayfarer_backend.repository;

import aj.org.objectweb.asm.commons.Remapper;
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
    @Query("DELETE FROM RefreshToken r WHERE r.user.id = :userId")
    void deleteByUserId(Integer userId);

    @Query("SELECT RefreshToken from RefreshToken r WHERE r.user.email = :email")
    RefreshToken findByUserEmail(String email);
}