package com.wayfarer.wayfarer_backend.dto.hike_dto;

import com.wayfarer.wayfarer_backend.model.HikeDifficulty;

public record HikeSearchCriteria(
        Double latitude,
        Double longitude,
        Double radiusMeters,
        HikeDifficulty difficulty,
        Boolean backToStart,
        Double minDistanceMeters,
        Double maxDistanceMeters,
        Double minElevationGain,
        Double maxElevationGain,
        Double minElevationLoss,
        Double maxElevationLoss,
        Integer minDurationSeconds,
        Integer maxDurationSeconds
){
    public void validate(){
        if (minDistanceMeters != null && maxDistanceMeters != null
                && minDistanceMeters > maxDistanceMeters) {
            throw new IllegalArgumentException("minDistanceMeters must be <= maxDistanceMeters");
        }
        if (minElevationGain != null && maxElevationGain != null
                && minElevationGain > maxElevationGain) {
            throw new IllegalArgumentException("minElevationGain must be <= maxElevationGain");
        }
        if (minElevationLoss != null && maxElevationLoss != null
                && minElevationLoss > maxElevationLoss) {
            throw new IllegalArgumentException("minElevationLoss must be <= maxElevationLoss");
        }
        if (minDurationSeconds != null && maxDurationSeconds != null
                && minDurationSeconds > maxDurationSeconds) {
            throw new IllegalArgumentException("minDurationSeconds must be <= maxDurationSeconds");
        }
        if ((latitude == null) == (longitude != null)) {
            throw new IllegalArgumentException("latitude and longitude must be provided together");
        }
        if (latitude != null && radiusMeters == null) {
            throw new IllegalArgumentException("radiusMeters is required when latitude/longitude are provided");
        }
    }
}