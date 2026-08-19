package com.wayfarer.wayfarer_backend.mapper;

import com.wayfarer.wayfarer_backend.dto.MapProviderDto;
import com.wayfarer.wayfarer_backend.model.MapProvider;

public class MapProviderMapper {

    public MapProviderDto toDto(MapProvider provider){
        MapProviderDto dto = new MapProviderDto();
        dto.setName(provider.getName());
        dto.setUrl(provider.getUrl());
        dto.setAttribution(provider.getAttribution());
        dto.setIsDefault(provider.getIsDefault());
        dto.setInvertible(provider.getInvertible());

        return dto;
    }

}
