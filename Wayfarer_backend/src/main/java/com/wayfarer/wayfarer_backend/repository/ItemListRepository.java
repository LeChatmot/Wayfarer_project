package com.wayfarer.wayfarer_backend.repository;

import com.wayfarer.wayfarer_backend.model.ItemList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemListRepository extends JpaRepository<ItemList, Integer> {
    List<ItemList> findAllByUserId(Integer userId);
    Optional<ItemList> findByIdAndUserId(Integer id, Integer userId);
}