package com.wayfarer.wayfarer_backend.controller;

import com.wayfarer.wayfarer_backend.dto.MapProviderDto;
import com.wayfarer.wayfarer_backend.mapper.MapProviderMapper;
import com.wayfarer.wayfarer_backend.repository.MapProviderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/map-provider")
public class MapProviderController {

    private final MapProviderRepository mapProviderRepository;

    private final MapProviderMapper mapProviderMapper = new MapProviderMapper();

    public MapProviderController(MapProviderRepository mapProviderRepository){
        this.mapProviderRepository = mapProviderRepository;
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public List<MapProviderDto> getAllMapProvider() {
        return this.mapProviderRepository.findAll().stream()
                .map(this.mapProviderMapper::toDto)
                .toList();
    }

    @GetMapping("/plan-ign")
    @ResponseStatus(HttpStatus.OK)
    public MapProviderDto getPlanIGNMapProvider() {
        return this.mapProviderMapper.toDto(this.mapProviderRepository.findByName("Plan IGN V2"));
    }
}
