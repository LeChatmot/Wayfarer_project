package com.wayfarer.wayfarer_backend.repository;

import com.wayfarer.wayfarer_backend.model.MapProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MapProviderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MapProviderRepository mapProviderRepository;

    @Test
    void findAll_shouldReturnAllProviders() {
        MapProvider provider1 = new MapProvider();
        MapProvider provider2 = new MapProvider();
        entityManager.persist(provider1);
        entityManager.persist(provider2);
        entityManager.flush();

        List<MapProvider> providers = mapProviderRepository.findAll();

        assertThat(providers).hasSize(2);
        assertThat(providers).extracting("name").containsExactlyInAnyOrder("IGN", "OpenStreetMap");
    }

    @Test
    void findDefault() {
        MapProvider activeProvider = new MapProvider();
        activeProvider.setIsDefault(true);
        MapProvider inactiveProvider = new MapProvider();
        activeProvider.setIsDefault(true);
        entityManager.persist(activeProvider);
        entityManager.persist(inactiveProvider);
        entityManager.flush();

        List<MapProvider> activeProviders = mapProviderRepository.findAll();

        assertThat(activeProviders).hasSize(1);
        assertThat(activeProviders.get(0).getName()).isEqualTo("IGN");
    }

    @Test
    void save_shouldPersistAndReturnEntity() {
        MapProvider provider = new MapProvider();
        provider.setName("Satellite");
        MapProvider savedProvider = mapProviderRepository.save(provider);

        assertThat(savedProvider.getId()).isNotNull();
        assertThat(savedProvider.getName()).isEqualTo("Satellite");
    }
}