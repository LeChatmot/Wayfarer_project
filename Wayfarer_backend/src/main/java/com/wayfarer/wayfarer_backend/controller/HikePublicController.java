package com.wayfarer.wayfarer_backend.controller;

import com.wayfarer.wayfarer_backend.dto.PageResponse;
import com.wayfarer.wayfarer_backend.dto.hike_dto.HikePathResponse;
import com.wayfarer.wayfarer_backend.dto.hike_dto.HikeResponse;
import com.wayfarer.wayfarer_backend.dto.hike_dto.HikeSearchCriteria;
import com.wayfarer.wayfarer_backend.mapper.HikeMapper;
import com.wayfarer.wayfarer_backend.mapper.PageResponseMapper;
import com.wayfarer.wayfarer_backend.model.HikeDifficulty;
import com.wayfarer.wayfarer_backend.service.hike_service.HikeService;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/hikes")
public class HikePublicController {

    private final HikeMapper hikeMapper;
    private final HikeService hikeService;

    public HikePublicController(HikeMapper hikeMapper, HikeService hikeService) {
        this.hikeMapper = hikeMapper;
        this.hikeService = hikeService;
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<HikeResponse> search(
            @RequestParam(required = false) @DecimalMin("-90") @DecimalMax("90") Double latitude,
            @RequestParam(required = false) @DecimalMin("-90") @DecimalMax("90") Double longitude,
            @RequestParam(required = false) @Positive Double radiusMeters,
            @RequestParam(required = false) HikeDifficulty difficulty,
            @RequestParam(required = false) Boolean backToStart,
            @RequestParam(required = false) @PositiveOrZero Double minDistanceMeters,
            @RequestParam(required = false) @PositiveOrZero Double maxDistanceMeters,
            @RequestParam(required = false) @PositiveOrZero Double minElevationGain,
            @RequestParam(required = false) @PositiveOrZero Double maxElevationGain,
            @RequestParam(required = false) @PositiveOrZero Double minElevationLoss,
            @RequestParam(required = false) @PositiveOrZero Double maxElevationLoss,
            @RequestParam(required = false) @PositiveOrZero Integer minDurationSeconds,
            @RequestParam(required = false) @PositiveOrZero Integer maxDurationSeconds,
            Pageable pageable
    ) {
        HikeSearchCriteria criteria = new HikeSearchCriteria(
                latitude, longitude, radiusMeters, difficulty, backToStart,
                minDistanceMeters, maxDistanceMeters,
                minElevationGain, maxElevationGain,
                minElevationLoss, maxElevationLoss,
                minDurationSeconds, maxDurationSeconds
        );

        criteria.validate();

        Page<HikeResponse> hikes = this.hikeService.search(criteria, pageable).map(hikeMapper::toDto);
        return PageResponseMapper.toDto(hikes);
    }

    @GetMapping("/{id}")
    public HikeResponse getHike(@PathVariable Integer id){
        return hikeMapper.toDto(this.hikeService.getHike(id));
    }

    @GetMapping("/{id}/isCreator")
    public Boolean isUserCreatorOfHike(@PathVariable Integer id){
        return this.hikeService.isUserCreatorOfHike(id);
    }

    @GetMapping("/{id}/getPath")
    public HikePathResponse getPathHike(@PathVariable Integer id){
        return new HikePathResponse(this.hikeMapper.mapPath(this.hikeService.getPathFromHike(id)));
    }
}
