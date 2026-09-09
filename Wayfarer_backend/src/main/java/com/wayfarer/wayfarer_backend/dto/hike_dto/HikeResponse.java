package com.wayfarer.wayfarer_backend.dto.hike_dto;

import com.wayfarer.wayfarer_backend.model.HikeDifficulty;

public record HikeResponse(
        Integer id,
        String name,
        String description,
        boolean backToStart,
        double distanceMeters,
        double elevationGain,
        double elevationLoss,
        StartingPointResponse startingPoint,
        String startingPointName,
        String createdBy,
        Integer durationSeconds,
        HikeDifficulty difficulty,
        String previewImageLightUrl,
        String previewImageDarkUrl,
        boolean favorite
) { }