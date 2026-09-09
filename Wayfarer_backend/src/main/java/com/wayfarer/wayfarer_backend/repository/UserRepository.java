package com.wayfarer.wayfarer_backend.repository;

import com.wayfarer.wayfarer_backend.model.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmailHash(String email);

    boolean existsByUsername(String username);

    boolean existsByEmailHash(String email);
}