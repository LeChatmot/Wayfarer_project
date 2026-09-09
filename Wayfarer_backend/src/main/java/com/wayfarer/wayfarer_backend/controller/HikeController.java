package com.wayfarer.wayfarer_backend.controller;

import com.wayfarer.wayfarer_backend.dto.PageResponse;
import com.wayfarer.wayfarer_backend.dto.hike_dto.HikeCreateRequest;
import com.wayfarer.wayfarer_backend.dto.hike_dto.HikeResponse;
import com.wayfarer.wayfarer_backend.dto.hike_dto.HikeUpdateRequest;
import com.wayfarer.wayfarer_backend.mapper.HikeMapper;
import com.wayfarer.wayfarer_backend.mapper.PageResponseMapper;
import com.wayfarer.wayfarer_backend.service.hike_service.HikeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/hikes")
public class HikeController {

    private final HikeService hikeService;
    private final HikeMapper hikeMapper;

    public HikeController(HikeService hikeService, HikeMapper hikeMapper) {
        this.hikeService = hikeService;
        this.hikeMapper = hikeMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HikeResponse create(@Valid @RequestBody HikeCreateRequest request) {
        return hikeService.create(request);
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

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public HikeResponse updateHike(@PathVariable Integer id, @Valid @RequestBody HikeUpdateRequest request) throws AccessDeniedException {
        HikeResponse hikeResponse = this.hikeService.updateHike(id, request);
        return hikeResponse;
    }

    @DeleteMapping("/{id}/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteHike(@PathVariable Integer id){
        hikeService.deleteHike(id);
    }

    @GetMapping("/mines")
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<HikeResponse> getUserHikes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        Page<HikeResponse> hikeResponses = this.hikeService.searchUserHikes(page, size).map(this.hikeMapper::toDto);
        return PageResponseMapper.toDto(hikeResponses);
    }

    @GetMapping("/favorites")
    public PageResponse<HikeResponse> getMyFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<HikeResponse> hikeResponses = hikeService.getFavoritesByCurrentUser(page, size).map(this.hikeMapper::toDto);
        return PageResponseMapper.toDto(hikeResponses);
    }
}