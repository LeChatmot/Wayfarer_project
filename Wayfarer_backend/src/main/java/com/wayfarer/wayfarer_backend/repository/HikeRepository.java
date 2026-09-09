package com.wayfarer.wayfarer_backend.repository;

import com.wayfarer.wayfarer_backend.model.Hike;
import com.wayfarer.wayfarer_backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface HikeRepository extends JpaRepository<Hike, Integer>, JpaSpecificationExecutor<Hike> {
    Page<Hike> findAllByCreatorEquals(User creator, Pageable pageable);

    Hike findHikeById(Integer id);
}