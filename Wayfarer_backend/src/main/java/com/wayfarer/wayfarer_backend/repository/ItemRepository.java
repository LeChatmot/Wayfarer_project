package com.wayfarer.wayfarer_backend.repository;

import com.wayfarer.wayfarer_backend.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Integer> {
}