package com.wayfarer.wayfarer_backend.service.mapProvider_service;

import com.wayfarer.wayfarer_backend.model.MapProvider;
import com.wayfarer.wayfarer_backend.repository.MapProviderRepository;
import org.springframework.stereotype.Service;

@Service
public class MapProviderService {

    private final MapProviderRepository mapProviderRepository;

    public MapProviderService(MapProviderRepository mapProviderRepository) {
        this.mapProviderRepository = mapProviderRepository;
    }

    public MapProvider getMapProviderPlanOpenStreet(){
        return this.mapProviderRepository.findByName("Plan OpenStreetMap");
    }
}
