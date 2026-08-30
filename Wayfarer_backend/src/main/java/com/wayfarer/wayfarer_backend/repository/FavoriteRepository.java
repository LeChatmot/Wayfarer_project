package com.wayfarer.wayfarer_backend.repository;

import com.wayfarer.wayfarer_backend.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Favorite.FavoriteId> {
    List<Favorite> findAllByUserId(Integer userId);
    boolean existsByUserIdAndHikeId(Integer userId, Integer hikeId);
    void deleteByUserIdAndHikeId(Integer userId, Integer hikeId);
}