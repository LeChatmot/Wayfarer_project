package com.wayfarer.wayfarer_backend.dto.hike_dto;

public record StartingPointResponse(
        double lat,
        double lng,
        double alt
) { }
