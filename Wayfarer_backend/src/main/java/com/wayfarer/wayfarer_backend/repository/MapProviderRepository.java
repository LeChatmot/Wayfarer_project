package com.wayfarer.wayfarer_backend.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.wayfarer.wayfarer_backend.model.MapProvider;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MapProviderRepository extends JpaRepository<MapProvider, Integer> {
    MapProvider findByName(String name);
}
