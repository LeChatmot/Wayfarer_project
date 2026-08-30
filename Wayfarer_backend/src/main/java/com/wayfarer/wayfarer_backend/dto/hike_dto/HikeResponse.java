package com.wayfarer.wayfarer_backend.dto.hike_dto;

public record HikeResponse(
        Integer id,
        String name,
        String description,
        String creatorUsername,
        double distanceMeters,
        double elevationGain,
        double elevationLoss,
        Integer durationSeconds,
        boolean backToStart,
        boolean favorite
) {}