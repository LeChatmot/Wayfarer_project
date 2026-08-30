package com.wayfarer.wayfarer_backend.controller;

import com.wayfarer.wayfarer_backend.dto.hike_dto.HikeCreateRequest;
import com.wayfarer.wayfarer_backend.dto.hike_dto.HikeResponse;
import com.wayfarer.wayfarer_backend.service.hike.HikeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hikes")
public class HikeController {

    private final HikeService hikeService;

    public HikeController(HikeService hikeService) {
        this.hikeService = hikeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HikeResponse create(@Valid @RequestBody HikeCreateRequest request) {
        return hikeService.create(request);
    }

    @GetMapping("/nearby")
    public List<HikeResponse> nearby(@RequestParam double lat,
                                     @RequestParam double lon,
                                     @RequestParam(defaultValue = "10000") double radius) {
        return hikeService.findNearby(lat, lon, radius);
    }

    @PutMapping("/{id}/favorite")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFavorite(@PathVariable Integer id) {
        hikeService.addFavorite(id);
    }

    @DeleteMapping("/{id}/favorite")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFavorite(@PathVariable Integer id) {
        hikeService.removeFavorite(id);
    }
}